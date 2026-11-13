package com.hr.freerein.config;

import com.hr.freerein.entity.*;
import com.hr.freerein.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Makes a brand-new database demo-ready.
 *  - Starter support resources: inserted once, only if the table is empty.
 *  - Demo accounts + sample data: only if app.seed.demo-users=true (env SEED_DEMO_USERS).
 * Safe to run on every start-up: nothing is duplicated.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository users;
    private final ResourceRepository resources;
    private final CaseRepository cases;
    private final SafetyPlanRepository safetyPlans;
    private final SupportRequestRepository supportRequests;
    private final SupportMessageRepository supportMessages;
    private final PasswordEncoder encoder;

    @Value("${app.seed.demo-users:false}") private boolean seedDemoUsers;
    @Value("${app.seed.admin-email:admin@freerein.demo}") private String adminEmail;
    @Value("${app.seed.admin-password:Admin@12345}") private String adminPassword;
    @Value("${app.seed.demo-password:Demo@12345}") private String demoPassword;

    public DataSeeder(UserRepository users, ResourceRepository resources, CaseRepository cases,
                      SafetyPlanRepository safetyPlans, SupportRequestRepository supportRequests,
                      SupportMessageRepository supportMessages, PasswordEncoder encoder) {
        this.users = users;
        this.resources = resources;
        this.cases = cases;
        this.safetyPlans = safetyPlans;
        this.supportRequests = supportRequests;
        this.supportMessages = supportMessages;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        seedResources();
        if (seedDemoUsers) {
            seedDemoAccounts();
        }
    }

    private void seedResources() {
        if (resources.count() > 0) {
            return;
        }
        List<Resource> list = List.of(
            res("National Domestic Violence Hotline (US)", "emergency", 100,
                "Free, confidential support 24/7 for anyone affected by domestic violence.",
                "## National Domestic Violence Hotline (US)\n\nFree, confidential support **24/7**.\n\n- Call **1-800-799-7233**\n- Text **START** to **88788**\n\nIf you are in immediate danger, call your local emergency number.",
                "1-800-799-7233", "United States", "hotline,24/7"),
            res("Women Helpline (India) - 181", "emergency", 99,
                "24x7 helpline for women affected by violence. Dial 181.",
                "## Women Helpline (India)\n\nDial **181** at any time for support, information and referrals.\n\nOther numbers: **112** (police, fire, ambulance) and **1091** (women in distress).",
                "181", "India", "hotline,india"),
            res("Emergency Services (India) - 112", "emergency", 98,
                "Single emergency number for police, fire and ambulance. If you are in immediate danger, call 112.",
                "## Emergency: 112\n\nIf you or your children are in immediate danger, call **112**. Stay on the line and say clearly where you are.",
                "112", "India", "emergency,police"),
            res("National Domestic Abuse Helpline (UK)", "support_services", 90,
                "Free 24-hour helpline for women experiencing domestic abuse.",
                "## National Domestic Abuse Helpline (UK)\n\nFree, 24 hours a day: **0808 2000 247**.",
                "0808 2000 247", "United Kingdom", "hotline,uk"),
            res("Creating a safety plan", "health_safety", 80,
                "A personalised, practical plan to stay safer while living with, leaving or after an abusive relationship.",
                "## Why a safety plan?\n\nA safety plan lists **safe places**, **emergency contacts**, **important documents** and **steps** to take if you must leave quickly.\n\nUse the **Safety Plan** page in FreeRein to build yours - it is private to you.",
                null, "Online", "safety,planning"),
            res("Know your legal rights", "legal_rights", 70,
                "Protection orders, filing a complaint and getting legal aid.",
                "## Your legal options\n\n- You can ask the police to register a complaint.\n- You can apply for a **protection order**.\n- Free legal aid may be available.\n\nA **legal advisor** on FreeRein can walk you through the steps: open *Get Help* and choose *Legal Advice*.",
                null, "Online", "legal,protection-order"),
            res("Talk to a counsellor", "support_services", 60,
                "Trauma-informed emotional support from trained counsellors. You decide the pace.",
                "## Counselling\n\nOpen **Get Help** and choose *Counselling*, or message a counsellor directly from **Chat**.",
                null, "Online", "counselling,support"),
            res("Financial independence and aid", "financial_aid", 50,
                "Practical steps to protect your money, documents and access to financial support.",
                "## Protecting yourself financially\n\n- Keep copies of ID, bank details and income proof in a safe place.\n- Open an account in your own name if you can.\n- Ask a counsellor about emergency financial assistance schemes.",
                null, "Online", "money,aid"),
            res("Finding safe housing", "housing", 40,
                "Short-term shelter and longer-term housing options.",
                "## Safe housing\n\nShelters and safe houses offer short-term refuge. Ask a counsellor on FreeRein to help find options near you.",
                null, "Online", "shelter,housing"),
            res("Supporting children and family", "children_family", 30,
                "Helping children who live with abuse and keeping the family safe.",
                "## Children and family\n\nChildren are affected even when they are not hurt directly. Keep their routine steady, listen to them, and include them in age-appropriate safety planning.",
                null, "Online", "children,family"),
            res("Digital safety basics", "health_safety", 20,
                "Protect your phone, accounts and location from being monitored.",
                "## Digital safety\n\n- Use strong, unique passwords.\n- Review who can see your location.\n- Clear history if someone else may check your device.\n- Use the **Quick Exit** button on every page.",
                null, "Online", "digital,privacy")
        );
        resources.saveAll(list);
        log.info("Seeded {} starter resources", list.size());
    }

    private Resource res(String title, String category, int priority, String description, String content,
                         String contact, String location, String tags) {
        Resource r = new Resource();
        r.setTitle(title);
        r.setCategory(category);
        r.setPriority(priority);
        r.setDescription(description);
        r.setContent(content);
        r.setContactInfo(contact);
        r.setLocation(location);
        r.setTargetAudience("all");
        r.setPublished(true);
        r.setTags(java.util.Arrays.asList(tags.split(",")));
        r.setActive(true);
        return r;
    }

    private void seedDemoAccounts() {
        createUser("FreeRein Admin", adminEmail, adminPassword, User.Role.ADMIN);
        User counsellor = createUser("Dr. Asha Counsellor", "counsellor@freerein.demo", demoPassword, User.Role.COUNSELLOR);
        createUser("Ravi Legal Advisor", "legal@freerein.demo", demoPassword, User.Role.LEGAL_ADVISOR);
        User victim = createUser("Demo User", "victim@freerein.demo", demoPassword, User.Role.VICTIM);

        if (victim != null && cases.findByUserIdOrderByCreatedAtDesc(victim.getId()).isEmpty()) {
            Case c = new Case();
            c.setUser(victim);
            c.setTitle("Harassment at home - need guidance");
            c.setDescription("Repeated verbal abuse and threats. Looking for advice on next steps.");
            c.setStatus("OPEN");
            cases.save(c);

            SafetyPlan plan = new SafetyPlan();
            plan.setUser(victim);
            plan.setTitle("My emergency plan");
            plan.setSafePlaces("[\"A trusted friend's house\",\"Local women's shelter\"]");
            plan.setEmergencyContacts("[{\"name\":\"Emergency services\",\"phone\":\"112\",\"relationship\":\"Police\"},{\"name\":\"Women Helpline\",\"phone\":\"181\",\"relationship\":\"Helpline\"}]");
            plan.setImportantDocuments("[\"ID card\",\"Bank passbook\",\"Medical records\"]");
            plan.setSafetySteps("[\"Keep phone charged\",\"Pack a go-bag with ID and some cash\",\"Agree a code word with a trusted friend\"]");
            plan.setSupportServices("[{\"name\":\"Women Helpline\",\"phone\":\"181\",\"type\":\"Helpline\"}]");
            plan.setPersonalNotes("Leave early in the morning if possible.");
            safetyPlans.save(plan);

            SupportRequest req = new SupportRequest();
            req.setUser(victim);
            req.setSubject("I need someone to talk to");
            req.setType("counselling");
            req.setUrgency("MEDIUM");
            req.setDescription("I would like to talk to someone about what I am going through.");
            req.setStatus("PENDING");
            req = supportRequests.save(req);

            SupportMessage m = new SupportMessage();
            m.setSupportRequest(req);
            m.setSenderId(String.valueOf(victim.getId()));
            m.setSenderEmail(victim.getEmail());
            m.setSenderRole("victim");
            m.setContent("Hello, I need someone to talk to.");
            m.setFromSupporter(false);
            supportMessages.save(m);
            log.info("Seeded sample case, safety plan and support request for demo user");
        }
        log.warn("Demo accounts are enabled (SEED_DEMO_USERS=true). Disable them on a public deployment.");
    }

    private User createUser(String name, String email, String password, User.Role role) {
        return users.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(encoder.encode(password));
            u.setRole(role);
            u.setActive(true);
            return users.save(u);
        });
    }
}
