#!/usr/bin/env bash
# End-to-end API demo.  Usage: ./docs/demo.sh [BASE_URL]
# Requires the demo accounts (SEED_DEMO_USERS=true). Needs only curl.
BASE="${1:-http://localhost:8080}"
J='Content-Type: application/json'
tok() { sed -n 's/.*"token":"\([^"]*\)".*/\1/p'; }
say() { printf '\n\033[1;36m== %s\033[0m\n' "$1"; }

say "1. Health check"; curl -s "$BASE/api/health"; echo
say "2. Public resources"; curl -s "$BASE/api/resources" | head -c 400; echo " ..."
say "3. Login as victim"
V=$(curl -s -X POST "$BASE/api/auth/login" -H "$J" -d '{"email":"victim@freerein.demo","password":"Demo@12345"}' | tok)
echo "token: ${V:0:25}..."
say "4. Victim: who am I"; curl -s "$BASE/api/auth/me" -H "Authorization: Bearer $V"; echo
say "5. Victim opens a case"
curl -s -X POST "$BASE/api/cases" -H "$J" -H "Authorization: Bearer $V" \
  -d '{"title":"Need legal advice","description":"Want to know about protection orders"}'; echo
say "6. Victim asks for support"
curl -s -X POST "$BASE/api/support/requests" -H "$J" -H "Authorization: Bearer $V" \
  -d '{"subject":"Legal guidance","requestType":"legal_advice","urgency":"HIGH","description":"Please guide me on next steps"}'; echo
say "7. Victim tries the admin API  (expect 403)"
curl -s -o /dev/null -w "HTTP %{http_code}\n" "$BASE/api/admin/stats" -H "Authorization: Bearer $V"
say "8. Counsellor sees pending requests"
C=$(curl -s -X POST "$BASE/api/auth/login" -H "$J" -d '{"email":"counsellor@freerein.demo","password":"Demo@12345"}' | tok)
curl -s "$BASE/api/support/requests?status=PENDING" -H "Authorization: Bearer $C" | head -c 500; echo " ..."
say "9. Counsellor takes request #1 and replies"
curl -s -X PATCH "$BASE/api/support/requests/1/assign" -H "Authorization: Bearer $C" | head -c 300; echo
curl -s -X POST "$BASE/api/support/requests/1/messages" -H "$J" -H "Authorization: Bearer $C" \
  -d '{"content":"Hi, I am here to help. Are you safe right now?"}'; echo
say "10. Counsellor tries to read the victim's private safety plan  (expect 404)"
curl -s -o /dev/null -w "HTTP %{http_code}\n" "$BASE/api/safety-plans/1" -H "Authorization: Bearer $C"
say "11. Admin dashboard stats"
A=$(curl -s -X POST "$BASE/api/auth/login" -H "$J" -d '{"email":"admin@freerein.demo","password":"Admin@12345"}' | tok)
curl -s "$BASE/api/admin/stats" -H "Authorization: Bearer $A"; echo
