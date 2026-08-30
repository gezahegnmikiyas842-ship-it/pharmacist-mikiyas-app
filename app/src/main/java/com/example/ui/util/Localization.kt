package com.example.ui.util

import com.example.data.model.AppLanguage

object Localization {
    fun get(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.EN -> englishStrings[key] ?: key
            AppLanguage.OM -> oromooStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.AM -> amharicStrings[key] ?: englishStrings[key] ?: key
        }
    }

    private val englishStrings = mapOf(
        "app_title" to "Pharmacist Mikiyas",
        "tagline" to "Clinical Pharmacist | Digital Health Innovator",
        "dev_name" to "Mikiyas Gezahegn",
        "dev_credentials" to "Clinical Pharmacist | Digital Health Innovator",
        "nav_home" to "Home",
        "nav_tools" to "Clinical Tools",
        "nav_drugs" to "Drug Hub",
        "nav_ai" to "AI Assistant",
        "nav_more" to "Hub & Learn",
        
        // Home
        "hero_greeting" to "Bridging Healthcare & Technology",
        "hero_sub" to "Empowering clinicians, pharmacists, and students with evidence-based digital health tools, AI clinical decision support, and pharmacotherapy calculators.",
        "stat_experience" to "Clinical Years",
        "stat_accuracy" to "Clinical Precision",
        "stat_calculators" to "Smart Calculators",
        "stat_drugs" to "Drug Database",
        "featured_services" to "Featured Clinical Services",
        "service_calculators" to "Point-of-Care Calculators",
        "service_calculators_desc" to "Cockcroft-Gault, eGFR CKD-EPI, Pediatric dosing, and Infusion rates.",
        "service_interactions" to "Drug Interaction Checker",
        "service_interactions_desc" to "Multi-agent interaction screening with severity analysis and management.",
        "service_ai" to "AI Pharmacy Assistant",
        "service_ai_desc" to "Powered by Google Gemini for clinical pearls, counseling, and guidelines.",
        "service_learning" to "Education & Learning Center",
        "service_learning_desc" to "MCQ quizzes, flashcards, and study modules across health disciplines.",
        
        // Calculators
        "calc_title" to "Clinical Calculators",
        "calc_bmi" to "BMI & Ideal Body Weight",
        "calc_crcl" to "Creatinine Clearance (CrCl)",
        "calc_egfr" to "eGFR (CKD-EPI 2021)",
        "calc_pediatric" to "Pediatric Dose Calculator",
        "calc_infusion" to "IV Infusion & Drop Rate",
        "calc_pregnancy" to "Pregnancy Wheel & EDD",
        "calc_gcs" to "Glasgow Coma Scale (GCS)",
        "calc_dosage" to "Drug Dosage & Unit Converter",
        "btn_calculate" to "Calculate Result",
        "btn_clear" to "Reset",
        "btn_save_result" to "Save to Records",
        "calc_history" to "Calculation History",
        "no_history" to "No saved calculations yet",

        // Drug Info
        "drug_search_hint" to "Search generic or brand name...",
        "drug_interactions" to "Interaction Checker",
        "select_drugs_to_check" to "Select 2 or more medicines to evaluate potential interactions:",
        "btn_check_interactions" to "Analyze Drug Interactions",
        "no_interactions_found" to "No documented severe drug-drug interactions detected between selected agents.",

        // AI Assistant
        "ai_title" to "Clinical AI Assistant",
        "ai_disclaimer" to "Educational & Clinical Decision Support. Not a substitute for personalized clinical evaluation.",
        "ai_input_hint" to "Ask about drug dosages, interactions, renal adjustments, or disease guidelines...",
        "ai_ask_button" to "Consult AI",

        // About & Profile
        "about_title" to "About Mikiyas Gezahegn",
        "bio_headline" to "Clinical Pharmacist & Full-Stack Engineer",
        "education_title" to "Education & Qualifications",
        "certifications_title" to "Certifications & Licensure",
        "skills_title" to "Core Competencies",
        "research_title" to "Research & Publications",
        "learning_title" to "Learning Center & Quizzes",
        "blog_title" to "Pharmacy Clinical Blog",
        "contact_title" to "Contact & Consultation",
        "send_message" to "Send Message",
        "admin_dashboard" to "Admin & Provider Dashboard"
    )

    private val oromooStrings = mapOf(
        "app_title" to "Farmaasistii Mikiyas",
        "tagline" to "Farmaasistii Kilinikaalaa | Hawaasa Dijitaalaa",
        "dev_name" to "Mikiyas Gazaahanyi",
        "dev_credentials" to "Farmaasistii Kilinikaalaa | Hawaasa Dijitaalaa",
        "nav_home" to "Mana",
        "nav_tools" to "Meeshaalee Kilinikaalaa",
        "nav_drugs" to "Qorichoota",
        "nav_ai" to "Gargaaraa AI",
        "nav_more" to "Barnootaa fi Dabalata",
        
        "hero_greeting" to "Fayyaa fi Teeknooloojii Walitti Qindeessuu",
        "hero_sub" to "Ogeeyyii fayyaa, farmaasistoota fi barattootaaf meeshaalee dijitaalaa fi AI gargaaran dhiyeessuu.",
        "stat_experience" to "Waggaa Muuxannoo",
        "stat_accuracy" to "Sirrummaa Kilinikaalaa",
        "stat_calculators" to "Shallaggii Qorichaa",
        "stat_drugs" to "Kuusaa Qorichaa",
        "featured_services" to "Tajaajiloota Kilinikaalaa",
        "service_calculators" to "Shallaggiiwwan Kilinikaalaa",
        "service_calculators_desc" to "Shallaggii CrCl, eGFR, Dooksisii Daa'immanii fi dhangala'aa IV.",
        "service_interactions" to "Wal-nyaatinsa Qorichootaa",
        "service_interactions_desc" to "Qorichoota walitti fudhataman adda baasuu fi gorsa kennuu.",
        "service_ai" to "Gargaaraa Farmaasii AI",
        "service_ai_desc" to "Google Gemini fayyadamuun gorsa qorichaa fi odeeffannoo kennu.",
        "service_learning" to "Wiirtuu Barnootaa",
        "service_learning_desc" to "Gaaffilee qormaataa (MCQ), kaardiiwwan yaadaa fi barnoota fayyaa.",
        
        "calc_title" to "Shallaggiiwwan Kilinikaalaa",
        "calc_bmi" to "BMI fi Ulfaatina Qaamaa",
        "calc_crcl" to "Qulqullina Kiriyeatiniinii (CrCl)",
        "calc_egfr" to "eGFR (CKD-EPI 2021)",
        "calc_pediatric" to "Hamma Qoricha Daa'immanii",
        "calc_infusion" to "Dhangala'aa IV fi Saffisa Dhangala'aa",
        "calc_pregnancy" to "Shallaggii Ulfaa fi Guyyaa Dhala",
        "calc_gcs" to "Sadarkaa Sammuu (GCS)",
        "calc_dosage" to "Hamma Qorichaa fi Jijjiirraa Yuuniitii",
        "btn_calculate" to "Shallagi",
        "btn_clear" to "Haqi",
        "btn_save_result" to "Galmeessi",
        "calc_history" to "Seenaa Shallaggii",
        "no_history" to "Shallaggiin olkaayame hin jiru",

        "drug_search_hint" to "Maqaa qorichaa barbaadi...",
        "drug_interactions" to "Wal-nyaatinsa Qorichaa Sakatta'i",
        "select_drugs_to_check" to "Qorichoota 2 yookiin isaa ol filadhu:",
        "btn_check_interactions" to "Wal-nyaatinsa Sakatta'i",
        "no_interactions_found" to "Wal-nyaatinsi hamaa ta'e hin argamne.",

        "ai_title" to "Gargaaraa Kilinikaalaa AI",
        "ai_disclaimer" to "Barnootaaf qofa kan qophaa'e. Yaala hakiimaa hin bakka bu'u.",
        "ai_input_hint" to "Waa'ee hamma qorichaa, wal-nyaatinsa, yookiin qajeelfama gaafadhu...",
        "ai_ask_button" to "AI Gaafadhu",

        "about_title" to "Waa'ee Mikiyas Gazaahanyi",
        "bio_headline" to "Farmaasistii Kilinikaalaa fi Injinara Sooftiweerii",
        "education_title" to "Barnoota fi Qorannoo",
        "certifications_title" to "Sartifikeetiiwwan",
        "skills_title" to "Dandeettiiwwan Ijoo",
        "research_title" to "Qorannoo fi Maxxansaa",
        "learning_title" to "Wiirtuu Barnootaa",
        "blog_title" to "Biloogii Farmaasii",
        "contact_title" to "Qunnamtii",
        "send_message" to "Ergaa Ergi",
        "admin_dashboard" to "Daashboordii Bulchiinsaa"
    )

    private val amharicStrings = mapOf(
        "app_title" to "ፋርማሲስት ሚኪያስ",
        "tagline" to "ክሊኒካል ፋርማሲስት | የዲጂታል ጤና ፈጣሪ",
        "dev_name" to "ሚኪያስ ገዛኸኝ",
        "dev_credentials" to "ክሊኒካል ፋርማሲስት | የዲጂታል ጤና ፈጣሪ",
        "nav_home" to "መነሻ",
        "nav_tools" to "ክሊኒካል መሳሪዎች",
        "nav_drugs" to "የመድኃኒት ማዕከል",
        "nav_ai" to "AI ረዳት",
        "nav_more" to "ትምህርት እና ማዕከል",
        
        "hero_greeting" to "ጤናን እና ቴክኖሎጂን በቅንጅት ማሳደግ",
        "hero_sub" to "ለጤና ባለሙያዎች፣ ለፋርማሲስቶች እና ለተማሪዎች በማስረጃ ላይ የተመሰረቱ የዲጂታል ጤና መሳሪያዎች፣ የክሊኒካል AI እና የፋርማኮቴራፒ ማስያዎችን ያቀርባል።",
        "stat_experience" to "የልምድ ዓመታት",
        "stat_accuracy" to "ክሊኒካል ትክክለኛነት",
        "stat_calculators" to "ስማርት ማስያዎች",
        "stat_drugs" to "የመድኃኒት መረጃ",
        "featured_services" to "ዋና ዋና ክሊኒካል አገልግሎቶች",
        "service_calculators" to "የነጥብ-እንክብካቤ ማስያዎች",
        "service_calculators_desc" to "የኩላሊት ማጣሪያ (CrCl, eGFR)፣ የህፃናት መጠን እና የፈሳሽ ፍጥነት ማስያዎች።",
        "service_interactions" to "የመድኃኒት መስተጋብር መመርመሪያ",
        "service_interactions_desc" to "መድኃኒቶች እርስ በእርስ ሲወሰዱ ሊፈጥሩ የሚችሉትን የጎንዮሽ ጉዳት መለየት።",
        "service_ai" to "የፋርማሲ AI ረዳት",
        "service_ai_desc" to "በGoogle Gemini የታገዘ የክሊኒካል ምክር እና የመድኃኒት መመሪያ ረዳት።",
        "service_learning" to "የትምህርት እና ማሰልጠኛ ማዕከል",
        "service_learning_desc" to "የፈተና ጥያቄዎች (MCQs)፣ ፍላሽ ካርዶች እና የጤና ማስታወሻዎች።",
        
        "calc_title" to "ክሊኒካል ማስያዎች",
        "calc_bmi" to "የሰውነት ክብደት ማውጫ (BMI & IBW)",
        "calc_crcl" to "የኩላሊት ማጣሪያ (CrCl)",
        "calc_egfr" to "eGFR (CKD-EPI 2021)",
        "calc_pediatric" to "የህፃናት መድኃኒት መጠን",
        "calc_infusion" to "የደም ስር ፈሳሽ ፍጥነት",
        "calc_pregnancy" to "የእርግዝና እና የመውለጃ ቀን",
        "calc_gcs" to "የንቃት ደረጃ (Glasgow Coma Scale)",
        "calc_dosage" to "የመድኃኒት መጠን እና ልወጣ",
        "btn_calculate" to "አስላ",
        "btn_clear" to "አጥፋ",
        "btn_save_result" to "ውጤቱን መዝግብ",
        "calc_history" to "የቀድሞ ስሌቶች ታሪክ",
        "no_history" to "የተመዘገበ ስሌት እስካሁን የለም",

        "drug_search_hint" to "የመድኃኒት ስም ፈልግ...",
        "drug_interactions" to "የመድኃኒት መስተጋብር ቼከር",
        "select_drugs_to_check" to "ለማነፃፀር 2 ወይም ከዚያ በላይ መድኃኒቶችን ይምረጡ:",
        "btn_check_interactions" to "መስተጋብርን መርምር",
        "no_interactions_found" to "በተመረጡት መድኃኒቶች መካከል አደገኛ መስተጋብር አልተገኘም።",

        "ai_title" to "ክሊኒካል AI ረዳት",
        "ai_disclaimer" to "ለትምህርታዊ እና ክሊኒካል እገዛ ብቻ የተዘጋጀ። የህክምና ባለሙያ ምርመራን አይተካም።",
        "ai_input_hint" to "ስለ መድኃኒት መጠን፣ መስተጋብር ወይም መመሪያዎች ይጠይቁ...",
        "ai_ask_button" to "AI ጠይቅ",

        "about_title" to "ስለ ሚኪያስ ገዛኸኝ",
        "bio_headline" to "ክሊኒካል ፋርማሲስት እና የሶፍትዌር መሃንዲስ",
        "education_title" to "ትምህርት እና ብቃት",
        "certifications_title" to "የሙያ ማረጋገጫዎች",
        "skills_title" to "ዋና ዋና ክህሎቶች",
        "research_title" to "ጥናቶች እና ህትመቶች",
        "learning_title" to "የትምህርት ማዕከል",
        "blog_title" to "የፋርማሲ ክሊኒካል ብሎግ",
        "contact_title" to "አድራሻ እና ግንኙነት",
        "send_message" to "መልእክት ላክ",
        "admin_dashboard" to "የአስተዳዳሪ ዳሽቦርድ"
    )
}
