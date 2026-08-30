package com.example.data.repository

import com.example.data.local.PharmacyDao
import com.example.data.model.*
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.flow.Flow

class PharmacyRepository(private val dao: PharmacyDao) {

    val allDrugs: Flow<List<DrugItem>> = dao.getAllDrugs()
    val savedDrugs: Flow<List<DrugItem>> = dao.getSavedDrugs()
    val calcHistory: Flow<List<CalcHistoryEntity>> = dao.getAllCalcHistory()
    val allArticles: Flow<List<ArticleItem>> = dao.getAllArticles()
    val allMessages: Flow<List<ContactMessageEntity>> = dao.getAllMessages()

    suspend fun searchDrugs(query: String): Flow<List<DrugItem>> = dao.searchDrugs(query)

    suspend fun toggleSaveDrug(drug: DrugItem) {
        dao.updateDrug(drug.copy(isSaved = !drug.isSaved))
    }

    suspend fun insertDrug(drug: DrugItem) {
        dao.insertDrug(drug)
    }

    suspend fun deleteDrug(drug: DrugItem) {
        dao.deleteDrug(drug)
    }

    suspend fun saveCalcResult(calcType: String, title: String, inputSummary: String, resultValue: String, interpretation: String) {
        dao.insertCalcHistory(
            CalcHistoryEntity(
                calcType = calcType,
                title = title,
                inputSummary = inputSummary,
                resultValue = resultValue,
                interpretation = interpretation
            )
        )
    }

    suspend fun deleteCalcHistory(id: Int) {
        dao.deleteCalcHistoryById(id)
    }

    suspend fun clearCalcHistory() {
        dao.clearCalcHistory()
    }

    suspend fun toggleBookmarkArticle(article: ArticleItem) {
        dao.updateArticle(article.copy(isBookmarked = !article.isBookmarked))
    }

    suspend fun insertArticle(article: ArticleItem) {
        dao.insertArticle(article)
    }

    suspend fun deleteArticle(article: ArticleItem) {
        dao.deleteArticle(article)
    }

    suspend fun sendMessage(name: String, email: String, subject: String, message: String) {
        dao.insertMessage(
            ContactMessageEntity(
                name = name,
                email = email,
                subject = subject,
                message = message
            )
        )
    }

    suspend fun deleteMessage(id: Int) {
        dao.deleteMessage(id)
    }

    suspend fun askGemini(prompt: String, history: List<Pair<String, Boolean>> = emptyList()): String {
        return GeminiClient.askClinicalAssistant(prompt, history)
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seeding initial medicines if needed
        dao.insertDrugs(initialDrugsList)
        dao.insertArticles(initialArticlesList)
    }

    // Static Drug Interactions Matrix
    fun checkInteractions(selectedDrugNames: List<String>): List<DrugInteractionCheckResult> {
        val results = mutableListOf<DrugInteractionCheckResult>()
        val normalized = selectedDrugNames.map { it.trim().lowercase() }

        for (i in 0 until normalized.size) {
            for (j in i + 1 until normalized.size) {
                val drug1 = normalized[i]
                val drug2 = normalized[j]

                val match = knownInteractionsDatabase.firstOrNull { interaction ->
                    (interaction.drugA.lowercase() in drug1 && interaction.drugB.lowercase() in drug2) ||
                    (interaction.drugA.lowercase() in drug2 && interaction.drugB.lowercase() in drug1)
                }

                if (match != null) {
                    results.add(match)
                }
            }
        }
        return results
    }

    companion object {
        val initialDrugsList = listOf(
            DrugItem(
                id = "drug-01",
                genericName = "Metformin Hydrochloride",
                brandNames = "Glucophage, Fortamet, Glumetza",
                category = "Endocrine / Antidiabetic",
                moa = "Decreases hepatic glucose production, decreases intestinal absorption of glucose, and improves insulin sensitivity by increasing peripheral glucose uptake and utilization.",
                standardDosage = "Initial: 500 mg PO BID or 850 mg PO daily with meals. Titrate by 500 mg weekly up to max 2000-2500 mg/day in divided doses.",
                contraindications = "Severe renal impairment (eGFR < 30 mL/min/1.73m²), metabolic acidosis including diabetic ketoacidosis, hypersensitivity.",
                sideEffects = "Gastrointestinal distress (diarrhea, nausea, abdominal cramping), metallic taste, Vitamin B12 deficiency (long-term), rare lactic acidosis.",
                pregnancyCategory = "B",
                lactationSafety = "Compatible with monitoring (minimal excretion into breast milk).",
                knownInteractions = "Iodinated contrast media (withhold 48h prior/post), Cimetidine, Furosemide, Alcohol (potentiates lactic acidosis risk).",
                storage = "Store at 20°C to 25°C (68°F to 77°F). Protect from light and moisture.",
                counselingPoints = "Take with meals to minimize stomach upset. Swallow extended-release tablets whole. Avoid excessive alcohol intake. Discontinue before radiographic contrast procedures.",
                isSaved = true
            ),
            DrugItem(
                id = "drug-02",
                genericName = "Lisinopril",
                brandNames = "Prinivil, Zestril, Qbrelis",
                category = "Cardiovascular / ACE Inhibitor",
                moa = "Competitively inhibits angiotensin-converting enzyme (ACE), preventing conversion of angiotensin I to angiotensin II, causing vasodilation and reduced aldosterone secretion.",
                standardDosage = "Hypertension: 10 mg PO once daily (initial 5 mg if on diuretic), titrate to 20-40 mg daily. Heart Failure: 2.5-5 mg daily, titrate to target 20-40 mg daily.",
                contraindications = "History of ACE-inhibitor-induced angioedema, hereditary or idiopathic angioedema, pregnancy (2nd & 3rd trimesters), concomitant aliskiren in diabetes.",
                sideEffects = "Dry non-productive cough (bradykinin accumulation), hyperkalemia, hypotension/dizziness, acute kidney injury, angioedema.",
                pregnancyCategory = "D",
                lactationSafety = "Caution advised; limited human data, monitor infant blood pressure.",
                knownInteractions = "Potassium supplements, Spironolactone, NSAIDs (blunts antihypertensive effect & worsens renal risk), Lithium (increased lithium toxicity).",
                storage = "Store at controlled room temperature 15°C to 30°C (59°F to 86°F).",
                counselingPoints = "Rise slowly from sitting position to avoid dizziness. Do not use potassium salt substitutes without consulting pharmacist. Report swelling of face, lips, tongue, or breathing difficulty immediately.",
                isSaved = true
            ),
            DrugItem(
                id = "drug-03",
                genericName = "Atorvastatin Calcium",
                brandNames = "Lipitor, Torvast",
                category = "Cardiovascular / HMG-CoA Reductase Inhibitor",
                moa = "Competitively inhibits 3-hydroxy-3-methylglutaryl-coenzyme A (HMG-CoA) reductase, the rate-limiting enzyme in cholesterol synthesis, upregulating LDL receptors and clearing plasma LDL.",
                standardDosage = "High-intensity statin therapy: 40 mg to 80 mg PO once daily. Moderate-intensity: 10 mg to 20 mg PO once daily. Take anytime of day with or without food.",
                contraindications = "Active liver disease, unexplained persistent elevations of hepatic transaminases, pregnancy, breastfeeding, hypersensitivity.",
                sideEffects = "Myalgia, arthralgia, elevated hepatic transaminases, diarrhea, nasopharyngitis, rare rhabdomyolysis.",
                pregnancyCategory = "X",
                lactationSafety = "Contraindicated due to essential role of cholesterol in infant development.",
                knownInteractions = "Strong CYP3A4 inhibitors (Clarithromycin, Itraconazole, Ketoconazole, Protease inhibitors), Gemfibrozil, Grapefruit juice (>1.2 L/day).",
                storage = "Store at 20°C to 25°C (68°F to 77°F).",
                counselingPoints = "Take once daily consistently. Avoid excessive grapefruit consumption. Promptly report unexplained muscle pain, tenderness, or weakness, especially if accompanied by dark urine or fever.",
                isSaved = false
            ),
            DrugItem(
                id = "drug-04",
                genericName = "Warfarin Sodium",
                brandNames = "Coumadin, Jantoven, Marevan",
                category = "Hematology / Anticoagulant",
                moa = "Inhibits Vitamin K epoxide reductase complex 1 (VKORC1), depleting functional vitamin K reserves and inhibiting synthesis of clotting factors II, VII, IX, X and proteins C and S.",
                standardDosage = "Individualized based on target INR (usually 2.0-3.0 for DVT/PE/AFib; 2.5-3.5 for mechanical mitral valves). Initial dose 2.5 to 5 mg PO daily.",
                contraindications = "Active major bleeding, hemorrhagic tendencies, severe hepatic disease, uncontrolled severe hypertension, pregnancy (except mechanical heart valves at high risk).",
                sideEffects = "Bleeding (major/minor), skin necrosis/gangrene (purple toe syndrome), hypersensitivity, alopecia.",
                pregnancyCategory = "X",
                lactationSafety = "Compatible (lipophilic with low excretion into breast milk).",
                knownInteractions = "Amiodarone, Fluconazole, Bactrim, Ciprofloxacin, NSAIDs/Aspirin, St. John's Wort, Rifampin, leafy greens (Vitamin K variability).",
                storage = "Store at 20°C to 25°C (68°F to 77°F). Protect from light.",
                counselingPoints = "Maintain consistent dietary vitamin K intake. Take at the same time each evening. Regular INR monitoring is essential. Avoid OTC NSAIDs/Aspirin without clinical guidance. Report unusual bruising or bleeding.",
                isSaved = true
            ),
            DrugItem(
                id = "drug-05",
                genericName = "Amoxicillin / Clavulanate Potassium",
                brandNames = "Augmentin, Clavam, Curam",
                category = "Infectious Disease / Beta-Lactam Antibiotic",
                moa = "Amoxicillin inhibits bacterial cell wall synthesis during active replication; clavulanic acid is a beta-lactamase inhibitor that protects amoxicillin from enzymatic degradation.",
                standardDosage = "Adults: 500/125 mg TID or 875/125 mg BID PO for 7-14 days. Pediatric: 25-45 mg/kg/day (based on amoxicillin) divided Q12H for mild-moderate infections.",
                contraindications = "History of severe hypersensitivity (anaphylaxis) to penicillin/beta-lactams, history of Augmentin-associated cholestatic jaundice/hepatic dysfunction.",
                sideEffects = "Diarrhea (clavulanate-mediated), nausea, vomiting, candidiasis (oral/vaginal), maculopapular rash, elevated AST/ALT.",
                pregnancyCategory = "B",
                lactationSafety = "Compatible; monitor infant for gastrointestinal disturbances (diarrhea/thrush).",
                knownInteractions = "Probenecid (increases amoxicillin serum levels), Warfarin (may prolong INR), Allopurinol (increases incidence of rash), Oral contraceptives.",
                storage = "Tablets: Store at or below 25°C (77°F). Reconstituted suspension: Refrigerate (2°C to 8°C) and discard after 10 days.",
                counselingPoints = "Take at the start of a meal to enhance absorption and reduce GI distress. Complete full prescribed course even if symptoms improve. Shake liquid suspension well before each dose.",
                isSaved = false
            ),
            DrugItem(
                id = "drug-06",
                genericName = "Vancomycin",
                brandNames = "Vancocin, Firvanq",
                category = "Infectious Disease / Glycopeptide Antibiotic",
                moa = "Inhibits bacterial cell wall synthesis by binding with high affinity to the D-alanyl-D-alanine terminus of cell wall precursor units, blocking peptidoglycan polymerization.",
                standardDosage = "IV: 15-20 mg/kg actual body weight Q8-12H (adjusted for renal function, target AUC/MIC 400-600 or trough 15-20 mcg/mL). Oral (C. difficile only): 125 mg PO QID for 10 days.",
                contraindications = "Known hypersensitivity to vancomycin.",
                sideEffects = "Nephrotoxicity, ototoxicity, Vancomycin Infusion Reaction ('Red Man Syndrome' from rapid histamine release), neutropenia, phlebitis.",
                pregnancyCategory = "C",
                lactationSafety = "Compatible (minimal oral systemic absorption by infant).",
                knownInteractions = "Aminoglycosides, Piperacillin/Tazobactam (increased nephrotoxicity), Amphotericin B, Loop diuretics, Cisplatin.",
                storage = "Vials: Store at controlled room temperature. Reconstituted vials stable per manufacturer guidelines.",
                counselingPoints = "Infuse IV slowly over at least 60 minutes (or ≤10 mg/min for doses >1g) to prevent infusion reactions. Oral capsules do not treat systemic infections (strictly for C. diff colitis).",
                isSaved = false
            ),
            DrugItem(
                id = "drug-07",
                genericName = "Omeprazole",
                brandNames = "Prilosec, Losec, Omez",
                category = "Gastrointestinal / Proton Pump Inhibitor",
                moa = "Irreversibly binds to and inhibits the H+/K+ ATPase enzyme system (proton pump) of gastric parietal cells, suppressing both basal and stimulated gastric acid secretion.",
                standardDosage = "GERD / Erosive Esophagitis: 20 mg to 40 mg PO once daily before breakfast for 4-8 weeks. H. pylori eradication: 20 mg BID as part of triple/quadruple therapy.",
                contraindications = "Hypersensitivity to omeprazole or substituted benzimidazoles, concomitant use with rilpivirine-containing products.",
                sideEffects = "Headache, abdominal pain, diarrhea, flatulence, long-term risks: hypomagnesemia, Clostridioides difficile-associated diarrhea, Vitamin B12 deficiency, bone fracture risk.",
                pregnancyCategory = "C",
                lactationSafety = "Compatible with clinical monitoring.",
                knownInteractions = "Clopidogrel (decreased activation via CYP2C19 inhibition), Methotrexate, Digoxin, Ketoconazole/Itraconazole (decreased absorption due to increased pH).",
                storage = "Store at 20°C to 25°C (68°F to 77°F). Protect from light and moisture.",
                counselingPoints = "Take 30 to 60 minutes before the first meal of the day (usually breakfast). Swallow capsules whole; do not crush or chew beads. Avoid prolonged unnecessary therapy without clinical review.",
                isSaved = false
            ),
            DrugItem(
                id = "drug-08",
                genericName = "Levothyroxine Sodium",
                brandNames = "Synthroid, Levoxyl, Tirosint, Eltroxin",
                category = "Endocrine / Thyroid Hormone",
                moa = "Synthetic form of thyroxine (T4) that is deiodinated in peripheral tissues to triiodothyronine (T3), regulating metabolic processes, protein synthesis, and enzymatic activities.",
                standardDosage = "Full replacement: 1.6 mcg/kg/day (ideal body weight). Geriatric / CAD: start lower at 12.5 to 25 mcg/day. Titrate by 12.5-25 mcg every 6-8 weeks based on serum TSH.",
                contraindications = "Uncorrected adrenal insufficiency, acute myocardial infarction, untreated thyrotoxicosis, hypersensitivity.",
                sideEffects = "Hyperthyroidism symptoms with overtreatment (palpitations, tachycardia, arrhythmias, tremors, insomnia, weight loss, heat intolerance), bone mineral density loss.",
                pregnancyCategory = "A",
                lactationSafety = "Compatible; essential for normal infant development.",
                knownInteractions = "Calcium carbonate, Iron supplements, Multivitamins, Cholestyramine, PPIs, Sucralfate, Aluminum/magnesium antacids (separate by 4 hours).",
                storage = "Store at 20°C to 25°C (68°F to 77°F). Protect from light and moisture.",
                counselingPoints = "Take once daily in the morning on an empty stomach with a full glass of water, at least 30 to 60 minutes before breakfast. Separate from calcium and iron supplements by at least 4 hours.",
                isSaved = true
            )
        )

        val knownInteractionsDatabase = listOf(
            DrugInteractionCheckResult(
                drugA = "Warfarin",
                drugB = "Aspirin",
                severity = InteractionSeverity.MAJOR,
                mechanism = "Additive antihemostatic effects (inhibition of clotting factors combined with irreversible platelet inhibition) and gastric mucosal irritation.",
                clinicalEffect = "Significantly increased risk of major gastrointestinal and systemic bleeding.",
                management = "Avoid concurrent combination unless explicitly indicated for high-risk cardiac indications (e.g. recent mechanical valve + ACS). If required, monitor INR closely, add gastroprotective agent (PPI), and counsel on bleeding signs."
            ),
            DrugInteractionCheckResult(
                drugA = "Warfarin",
                drugB = "Amiodarone",
                severity = InteractionSeverity.MAJOR,
                mechanism = "Amiodarone potent inhibition of CYP2C9 and CYP3A4 decreases warfarin clearance, increasing S-warfarin active enantiomer concentrations.",
                clinicalEffect = "Marked prolongation of PT/INR, high risk of severe spontaneous hemorrhage.",
                management = "Empirically reduce warfarin maintenance dose by 30% to 50% upon initiating amiodarone. Monitor INR weekly until stable."
            ),
            DrugInteractionCheckResult(
                drugA = "Lisinopril",
                drugB = "Spironolactone",
                severity = InteractionSeverity.MAJOR,
                mechanism = "Combined inhibition of aldosterone by ACE inhibitor and aldosterone receptor blockade by potassium-sparing diuretic.",
                clinicalEffect = "Severe and potentially life-threatening hyperkalemia, cardiac conduction abnormalities.",
                management = "Check baseline serum potassium and creatinine. Monitor electrolytes at 1 week, 4 weeks, and periodically. Counsel patient to avoid potassium supplements and potassium salt substitutes."
            ),
            DrugInteractionCheckResult(
                drugA = "Metformin",
                drugB = "Iodinated Contrast",
                severity = InteractionSeverity.CRITICAL,
                mechanism = "Contrast-induced nephropathy leads to acute renal impairment, impairing renal metformin clearance and precipitating severe lactic acidosis.",
                clinicalEffect = "High mortality lactic acidosis.",
                management = "Withhold metformin at the time of or prior to iodinated radiocontrast procedures in patients with eGFR 30-60 mL/min/1.73m² or undergoing intra-arterial catheterization. Re-evaluate eGFR 48 hours post-procedure before resuming."
            ),
            DrugInteractionCheckResult(
                drugA = "Atorvastatin",
                drugB = "Clarithromycin",
                severity = InteractionSeverity.CRITICAL,
                mechanism = "Clarithromycin is a potent CYP3A4 and OATP1B1 inhibitor, markedly increasing atorvastatin plasma exposure (AUC increased up to 4.5-fold).",
                clinicalEffect = "Severe statin-induced myopathy and life-threatening rhabdomyolysis.",
                management = "Temporarily suspend atorvastatin during clarithromycin therapy, or substitute with non-CYP3A4 metabolized statin (e.g. Rosuvastatin or Pravastatin at reduced dose) or alternative macrolide (Azithromycin)."
            ),
            DrugInteractionCheckResult(
                drugA = "Levothyroxine",
                drugB = "Calcium Carbonate",
                severity = InteractionSeverity.MODERATE,
                mechanism = "Calcium binds to levothyroxine in the gastrointestinal tract, forming an insoluble chelate and decreasing levothyroxine bioavailability by 20-30%.",
                clinicalEffect = "Subtherapeutic thyroid hormone levels, elevated TSH, breakthrough hypothyroid symptoms.",
                management = "Instruct patient to separate levothyroxine and calcium/iron supplements by at least 4 hours. Recheck TSH in 6 to 8 weeks if timing changes."
            ),
            DrugInteractionCheckResult(
                drugA = "Omeprazole",
                drugB = "Clopidogrel",
                severity = InteractionSeverity.MAJOR,
                mechanism = "Omeprazole competitively inhibits hepatic CYP2C19, the primary enzyme converting clopidogrel prodrug into its active antiplatelet thiol metabolite.",
                clinicalEffect = "Reduced platelet inhibition, increased risk of stent thrombosis and adverse ischemic cardiovascular events.",
                management = "Avoid concurrent use. Use Pantoprazole or H2-receptor antagonist (Famotidine) if gastroprotection is required with clopidogrel."
            ),
            DrugInteractionCheckResult(
                drugA = "Vancomycin",
                drugB = "Piperacillin/Tazobactam",
                severity = InteractionSeverity.MAJOR,
                mechanism = "Synergistic tubular and interstitial nephrotoxicity mechanism when combined.",
                clinicalEffect = "Significantly higher incidence of acute kidney injury (AKI) compared to Vancomycin plus Cefepime or Meropenem.",
                management = "Monitor daily serum creatinine and Vancomycin AUC. Consider alternative anti-pseudomonal beta-lactams (Cefepime, Meropenem) in patients requiring empiric MRSA and Pseudomonas coverage."
            )
        )

        val initialArticlesList = listOf(
            ArticleItem(
                id = "art-01",
                title = "Evidence-Based Renal Dose Adjustments in Acute Kidney Injury",
                excerpt = "A comprehensive clinical guide on calculating CrCl vs eGFR and managing narrow-therapeutic-index antimicrobials during dynamic renal recovery.",
                content = """
                Acute kidney injury (AKI) is a frequent and complex clinical challenge in hospital pharmacotherapy. Traditional static dosing nomograms based on serum creatinine often fail because serum creatinine lags behind acute changes in glomerular filtration by 24 to 48 hours.
                
                Key Clinical Takeaways:
                1. Serum Creatinine Limitations: In rapidly declining kidney function, measured SCr underestimates renal impairment. Conversely, during AKI recovery, SCr overestimates impairment.
                2. Vancomycin and Aminoglycoside Dosing: Shift from trough-only monitoring to AUC24/MIC ratio targets (400-600 mg*h/L for Vancomycin) using Bayesian dosing software or two-point pharmacokinetic modeling.
                3. Hydrophilic vs Lipophilic Drugs: Hydrophilic antibiotics (beta-lactams, glycopeptides, aminoglycosides) distribute predominantly in extracellular fluid, which is often drastically increased in septic patients with fluid overload.
                4. Continuous Renal Replacement Therapy (CRRT): Understand the difference between CVVH, CVVHD, and CVVHDF. Filter clearance, effluent rates, and sieving coefficients dictate supplementary dosing.
                """.trimIndent(),
                category = "Clinical Pharmacotherapy",
                author = "Mikiyas Gezahegn, RPh",
                readTime = "6 min read",
                tags = "Renal, AKI, Vancomycin, Antimicrobial Stewardship, Pharmacokinetics",
                isBookmarked = true,
                likesCount = 48
            ),
            ArticleItem(
                id = "art-02",
                title = "Direct Oral Anticoagulants (DOACs) vs Warfarin: Perioperative Bridging Pearls",
                excerpt = "Optimizing anticoagulation management in surgical settings: when to hold, when to bridge, and reversal protocol updates.",
                content = """
                Perioperative management of oral anticoagulants requires a delicate balance between thromboembolic prevention and surgical bleeding risk.
                
                DOAC Management Timelines:
                - Apixaban & Rivaroxaban: For low-bleeding-risk procedures with normal renal function, hold 24 hours prior. For high-bleeding-risk surgery, hold 48 hours (extend to 72 hours if CrCl < 30 mL/min).
                - Dabigatran: Driven heavily by renal clearance. CrCl ≥ 50: hold 24-48h. CrCl 30-49: hold 48-72h. CrCl < 30: hold 72-96h.
                - Routine Heparin Bridging: Generally NOT recommended for DOACs due to predictable on/off pharmacokinetics and doubled bleeding rates without reduction in thromboembolism.
                
                Specific Reversal Agents:
                - Idarucizumab (Praxbind) for Dabigatran reversal (5g IV).
                - Andexanet alfa (Andexxa) for Factor Xa inhibitors in life-threatening bleeding.
                """.trimIndent(),
                category = "Cardiovascular & Hematology",
                author = "Mikiyas Gezahegn, RPh",
                readTime = "5 min read",
                tags = "Anticoagulation, DOACs, Warfarin, Surgery, Hematology",
                isBookmarked = false,
                likesCount = 37
            ),
            ArticleItem(
                id = "art-03",
                title = "Digital Health Solutions and Clinical AI in Hospital Pharmacy Practice",
                excerpt = "Exploring how AI-assisted clinical decision support, electronic health record interoperability, and mobile health tools transform patient outcomes.",
                content = """
                The integration of artificial intelligence and digital health technologies into clinical pharmacy workflows is revolutionizing modern medication therapy management.
                
                Core Digital Frontiers in Pharmacy:
                1. Automated Drug Interaction & Allergy Screening: Moving from alert-fatigue-inducing legacy systems to context-aware machine learning models.
                2. Predictive Analytics for Adverse Drug Events: Identifying high-risk patients for bleeding, acute renal failure, and opioid-induced respiratory depression before clinical deterioration.
                3. Telepharmacy & Mobile Patient Counseling: Bridging healthcare access disparities in rural and underserved regions through multilingual mobile digital health platforms.
                4. Smart Clinical Decision Support (CDS): Empowering clinical pharmacists with rapid point-of-care calculators, pharmacokinetic calculators, and evidence-based guideline summaries.
                """.trimIndent(),
                category = "Digital Health & AI",
                author = "Mikiyas Gezahegn, RPh",
                readTime = "7 min read",
                tags = "Digital Health, AI in Medicine, Telepharmacy, Clinical Informatics",
                isBookmarked = true,
                likesCount = 62
            )
        )

        val initialResearchList = listOf(
            ResearchItem(
                id = "res-01",
                title = "Assessment of Antimicrobial Stewardship Interventions on Prescribing Practices and Patient Outcomes in Tertiary Hospitals",
                authors = "Mikiyas Gezahegn, et al.",
                publicationVenue = "Journal of Clinical Pharmacy and Therapeutics",
                year = "2024",
                category = "Peer-Reviewed Publication",
                summary = "A prospective cohort study evaluating the implementation of clinical pharmacist-led prospective audit with feedback on carbapenem and third-generation cephalosporin utilization.",
                clinicalSignificance = "Demonstrated a 34% reduction in inappropriate broad-spectrum antimicrobial days of therapy (DOT) and significant decrease in hospital-acquired C. difficile infections without increasing 30-day readmissions."
            ),
            ResearchItem(
                id = "res-02",
                title = "Development of an AI-Powered Multilingual Clinical Decision Support Tool for Point-of-Care Pharmacotherapy in Developing Healthcare Settings",
                authors = "Mikiyas Gezahegn",
                publicationVenue = "International Conference on Digital Health & Clinical Informatics",
                year = "2025",
                category = "Conference Poster & Presentation",
                summary = "Presented an offline-capable mobile platform integrating verified clinical calculators, drug interaction models, and natural language AI query assistance tailored for low-resource health facilities.",
                clinicalSignificance = "Showcased high calculation accuracy (99.8%) and 42% faster clinical query resolution times for attending pharmacists and resident physicians."
            ),
            ResearchItem(
                id = "res-03",
                title = "Pharmacokinetic Variability of Vancomycin in Critically Ill Sepsis Patients: Comparative Study of Trough vs AUC24-Guided Dosing",
                authors = "Mikiyas Gezahegn, et al.",
                publicationVenue = "Critical Care Pharmacotherapy Review",
                year = "2024",
                category = "Clinical Research Summary",
                summary = "Comparative trial demonstrating reduced rates of acute kidney injury when utilizing individual pharmacokinetic Bayesian curve fitting versus traditional trough concentrations of 15-20 mcg/mL.",
                clinicalSignificance = "Confirmed a 48% lower incidence of nephrotoxicity while maintaining equivalent clinical cure rates for MRSA bacteremia."
            )
        )

        val quizQuestionsList = listOf(
            QuizQuestion(
                id = 1,
                category = "Pharmacy",
                question = "A 68-year-old male with chronic kidney disease (eGFR 22 mL/min) is diagnosed with type 2 diabetes. Which of the following antidiabetic agents is contraindicated?",
                options = listOf(
                    "Linagliptin",
                    "Metformin Hydrochloride",
                    "Glimepiride (adjusted dose)",
                    "Insulin Glargine"
                ),
                correctIndex = 1,
                rationale = "Metformin is contraindicated in patients with severe renal impairment (eGFR < 30 mL/min/1.73m²) due to the elevated risk of drug accumulation and life-threatening lactic acidosis. Linagliptin is primarily eliminated hepatically and requires no renal dose adjustment."
            ),
            QuizQuestion(
                id = 2,
                category = "Pharmacy",
                question = "Which enzyme is primarily inhibited by Omeprazole that causes a clinically significant reduction in the activation of Clopidogrel?",
                options = listOf(
                    "CYP3A4",
                    "CYP2D6",
                    "CYP2C19",
                    "CYP1A2"
                ),
                correctIndex = 2,
                rationale = "Omeprazole inhibits CYP2C19, which is the key cytochrome P450 isoenzyme responsible for converting clopidogrel prodrug to its active platelet-inhibiting metabolite, increasing cardiovascular event risks."
            ),
            QuizQuestion(
                id = 3,
                category = "Medicine",
                question = "According to the Cockcroft-Gault equation for estimating Creatinine Clearance, which factor is multiplied for female patients?",
                options = listOf(
                    "0.75",
                    "0.85",
                    "0.90",
                    "1.15"
                ),
                correctIndex = 1,
                rationale = "In the Cockcroft-Gault equation, the calculated CrCl for females is multiplied by 0.85 to account for lower average muscle mass and endogenous creatinine production relative to body weight compared to males."
            ),
            QuizQuestion(
                id = 4,
                category = "Nursing",
                question = "A patient is receiving an IV infusion of Vancomycin 1.5 g. The infusion is being run over 30 minutes, and the patient suddenly develops flushing, erythema of the upper chest and neck, and hypotension. What is the most appropriate initial nursing intervention?",
                options = listOf(
                    "Administer epinephrine IM immediately for anaphylaxis",
                    "Stop the infusion, assess airway/vitals, and notify provider (Vancomycin Infusion Reaction / Red Man Syndrome)",
                    "Increase IV rate to flush the line quickly",
                    "Change the IV catheter to a central line immediately"
                ),
                correctIndex = 1,
                rationale = "Rapid infusion of Vancomycin triggers direct non-immune histamine release causing 'Red Man Syndrome' (Vancomycin Infusion Reaction). The infusion must be stopped, vitals assessed, antihistamines considered, and subsequent doses infused over at least 90-120 minutes (≤10 mg/min)."
            ),
            QuizQuestion(
                id = 5,
                category = "Public Health",
                question = "What is the primary objective of an Antimicrobial Stewardship Program (ASP) in a healthcare institution?",
                options = listOf(
                    "To completely eliminate all broad-spectrum antibiotic use",
                    "To optimize clinical outcomes while minimizing unintended consequences including antimicrobial resistance, toxicity, and healthcare costs",
                    "To enforce generic substitution exclusively",
                    "To restrict antimicrobial prescribing solely to infectious disease specialists"
                ),
                correctIndex = 1,
                rationale = "Antimicrobial Stewardship Programs aim to ensure optimal antimicrobial selection, dosing, route, and duration of therapy to achieve the best clinical cure, prevent emergence of resistance (AMR), prevent C. diff, and optimize healthcare resource utilization."
            )
        )

        val flashcardsList = listOf(
            FlashcardItem(
                id = 1,
                category = "Pharmacology",
                frontPrompt = "What is the therapeutic target AUC24/MIC ratio for Vancomycin in serious MRSA infections?",
                backAnswer = "400 to 600 mg·h/L (assuming MIC ≤ 1 mcg/mL by broth microdilution).",
                clinicalPearls = "AUC-guided dosing significantly reduces acute kidney injury compared to traditional high trough targets (15-20 mcg/mL)."
            ),
            FlashcardItem(
                id = 2,
                category = "Calculations",
                frontPrompt = "State the Cockcroft-Gault formula for Creatinine Clearance (CrCl).",
                backAnswer = "CrCl (mL/min) = [(140 - Age) × Weight (kg)] / [72 × Serum Creatinine (mg/dL)] × (0.85 if female).",
                clinicalPearls = "Use Ideal Body Weight (IBW) for normal weight, Actual Body Weight (ABW) if underweight, and Adjusted Body Weight (AdjBW) if obese (BMI ≥ 30 or ABW > 120% IBW)."
            ),
            FlashcardItem(
                id = 3,
                category = "Clinical Pharmacy",
                frontPrompt = "Why must Levothyroxine be separated from oral iron and calcium supplements by at least 4 hours?",
                backAnswer = "Polyvalent cations (Fe²⁺, Ca²⁺) bind levothyroxine in the GI tract through chelation, severely reducing oral absorption.",
                clinicalPearls = "Instruct patients to take levothyroxine in the morning 30-60 min before breakfast, and mineral supplements in the afternoon/evening."
            ),
            FlashcardItem(
                id = 4,
                category = "Critical Care",
                frontPrompt = "What are the 3 component scores of the Glasgow Coma Scale (GCS) and their score ranges?",
                backAnswer = "1. Eye Opening (1-4)\n2. Verbal Response (1-5)\n3. Motor Response (1-6)\nTotal Range: 3 to 15.",
                clinicalPearls = "GCS ≤ 8 indicates severe brain injury requiring definitive airway management ('GCS of 8, intubate')."
            )
        )
    }
}
