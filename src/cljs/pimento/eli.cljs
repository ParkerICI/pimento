(ns pimento.eli
  )


(def elis
  [{:term "KRAS oncogene",
    :explanations
    {"a five-year old"
     "The KRAS oncogene is like a bossy switch in some cells. When it works right, it helps the cell grow and do its job. But sometimes, it gets stuck in the “on” position, making the cell grow too much, like a balloon that keeps getting bigger and bigger and won’t stop. This can lead to cancer.",
     "a high-school student"
     "The KRAS oncogene is a gene that gives instructions for making a protein that helps control how cells grow and divide. When this gene gets changed (mutated) in the wrong way, it can tell cells to grow too fast or not stop growing, which can lead to cancer. So, KRAS is like a light switch that, when stuck in the “on” position, makes cells multiply out of control.",
     "an intelligent layman"
     "The KRAS oncogene is a gene that makes a protein important for cell growth and division. Normally, it helps cells respond to signals to grow when needed. But if KRAS gets mutated (changed), it can become stuck in the “on” position, causing cells to keep growing uncontrollably. This can lead to cancer. KRAS mutations are common in cancers like lung, colon, and pancreatic cancer.",
     "a scientist in a different field"
     "The KRAS oncogene encodes a small GTPase that acts as a molecular switch in cell signaling pathways controlling growth and survival. Mutations in KRAS, particularly at codons 12, 13, or 61, result in constitutive activation of the protein, driving uncontrolled cell proliferation and tumor development. KRAS mutations are common in cancers such as pancreatic, colorectal, and lung adenocarcinomas and are a target of ongoing cancer drug development.",
     "an investor in biotech"
     "The KRAS oncogene is a mutated gene found in many cancers, most notably lung, colorectal, and pancreatic cancers. When mutated, KRAS drives uncontrolled cell growth and makes tumors aggressive and difficult to treat. Historically, KRAS was considered \"undruggable,\" but recent breakthroughs are leading to targeted therapies that could dramatically change treatment options and market potential for these cancers.",
     "a cancer patient"
     "The KRAS oncogene is a gene in your body that helps control how cells grow and divide. Sometimes, this gene can get a change (mutation) that makes it always turned on, telling cells to grow too much. When this happens, it can lead to cancer, because the cells don’t stop multiplying the way they’re supposed to."}}
   {:term "MIBI",
    :explanations
    {"a five-year old"
     "Multiplexed Ion Beam Imaging is like having a super camera that uses tiny, invisible particles to see lots of different colors and shapes in a piece of tissue. This helps scientists see many things at once, like where all the different \"helpers\" in your body are hiding, especially the ones fighting sickness.",
     "a high-school student"
     "Multiplexed Ion Beam Imaging (MIBI) is a special way to take detailed pictures of cells or tissues. Scientists add different metal tags to antibodies, which are proteins that can stick to specific parts of a cell (like markers for cancer or immune cells). Then, they shoot a beam of ions (tiny particles) at the sample. This knocks the metal tags off, and a machine detects which metals are there and where they are. This lets scientists see many different types of cells, all at once, in one tissue section—almost like making a super-detailed map showing many cell features at the same time.",
     "an intelligent layman"
     "Multiplexed Ion Beam Imaging (MIBI) is a technique used to look at many different proteins in a tissue sample at the same time. Scientists attach unique metal tags to antibodies that stick to specific proteins in the tissue. Then, they use a focused ion beam—like a super-fine laser—to knock these metal tags off the tissue, little by little. As the metals are released, a special machine detects them, telling researchers which proteins are present and exactly where they are in the tissue. This helps scientists get a detailed map of many different cell types and molecules, all at once, in the same tissue sample.",
     "a scientist in a different field"
     "Multiplexed Ion Beam Imaging (MIBI) is a tissue imaging technique that uses metal-tagged antibodies to label many different proteins simultaneously on a tissue section. A primary ion beam (usually oxygen or cesium) is directed onto the sample, which causes the release (sputtering) of the metal tags. These metals are then measured and mapped using mass spectrometry, generating highly multiplexed spatial images of dozens of proteins at subcellular resolution. This allows for comprehensive analysis of the cellular composition and spatial organization within complex tissues, such as tumors.",
     "a simple caveman"
     "MIBI is like special paint for the body. Doctors use it to see inside you. The paint goes into the blood, sticks to places that might be sick, like a tumor or infection. Then, doctor uses a magic eye (camera) to see where the paint glows. If it glows bright, there might be a problem there.",
     "an investor in biotech"
     "MIBI stands for “Multiplexed Ion Beam Imaging.” It’s a high-dimensional imaging technology that uses metal-tagged antibodies and a mass spectrometer to simultaneously visualize and quantify dozens of proteins or biomarkers in a single tissue section. In cancer immunotherapy, MIBI enables detailed mapping of the tumor microenvironment, helping identify immune cell subsets, their spatial relationships, and how they interact with tumor cells. This provides actionable insights for patient stratification, therapy response prediction, and novel target discovery.",
     "a cancer patient"
     "MIBI is a type of scan, also known as a sestamibi scan, where a small amount of a radioactive substance called sestamibi is injected into your vein. This substance travels through your body and gets taken up by certain tissues, including some types of cancer. Special images are then taken using a camera to see how the substance moves and where it collects. This helps your doctor see tumors or find out if cancer has spread."}}
   {:term "sgRNA",
    :explanations
    {"a five-year old"
     "sgRNA is like a little map that helps special scissors (called CRISPR) find the exact spot in a cell's DNA where they need to cut. It tells the scissors, \"Cut right here!\"",
     "a high-school student"
     "sgRNA stands for “single guide RNA.” It’s a short piece of RNA used in CRISPR gene editing. The sgRNA has two jobs: one part matches the DNA sequence you want to target, and the other part helps bring the Cas9 protein (the “molecular scissors”) to that exact spot in the DNA. Once they’re together, Cas9 can cut the DNA at the chosen location.",
     "an intelligent layman"
     "sgRNA stands for \"single guide RNA.\" It's a short piece of RNA used in CRISPR gene editing. Think of it as a GPS for the CRISPR system: it helps direct the editing machinery (Cas9 protein) to the exact spot in DNA that scientists want to cut or modify. The sgRNA matches up with the target DNA sequence, guiding Cas9 to the correct location so changes can be made accurately.",
     "a scientist in a different field"
     "sgRNA stands for \"single-guide RNA.\" It is a synthetic, engineered RNA molecule used in CRISPR-Cas9 gene editing systems. The sgRNA contains two components: a sequence (~20 nucleotides) that is complementary to a specific DNA target (the \"guide\" sequence), and a scaffold region that binds and directs the Cas9 endonuclease to the DNA. The sgRNA thus determines the specificity of where Cas9 will introduce a double-strand break in the genome.",
     "a simple caveman"
     "sgRNA is like a map that tells a cell’s cutting tool exactly where to go. It guides the tool to a spot in DNA, so it knows where to make a cut.",
     "an investor in biotech"
     "sgRNA stands for \"single guide RNA.\" It’s a short piece of RNA used in CRISPR gene editing. Think of it as a GPS: it guides the Cas9 protein to a specific spot in DNA, telling Cas9 exactly where to cut. The sgRNA makes this system precise, so scientists can target and edit genes at very specific locations.",
     "a cancer patient"
     "sgRNA stands for \"single guide RNA.\" It’s a small piece of genetic material used in a gene-editing tool called CRISPR. In cancer research and therapy, scientists use sgRNA to help direct CRISPR to a specific part of DNA in cancer cells, so they can change or fix genes that may be causing the cancer."}}
   {:term "T cell exhaustion",
    :explanations
    {"a five-year old"
     "T cell exhaustion is when the body’s fighter cells (T cells) get very tired because they have been fighting germs or cancer for too long. When they’re tired, they can’t fight as well anymore, like when you get tired after playing all day and need a rest.",
     "a high-school student"
     "T cell exhaustion happens when T cells—important immune cells that help fight infections and cancer—get overworked for a long time. Instead of staying strong, they become tired, weak, and stop working well. They don’t attack harmful cells as effectively, which can make it harder for the body to clear infections or fight cancer.",
     "an intelligent layman"
     "T cell exhaustion is when immune cells called T cells, which usually attack infections or cancer, become worn out after fighting for too long. When this happens, they stop working well—they don’t kill cancer cells or viruses effectively and show signs like having special “brake” proteins on their surface. This often happens during long battles with cancer or chronic infections, making it harder for the body to win.",
     "a scientist in a different field"
     "T cell exhaustion refers to a dysfunctional state that T cells enter after prolonged exposure to antigens, such as during chronic infections or cancer. Exhausted T cells have reduced ability to proliferate, secrete cytokines (like IFN-γ, TNF-α, and IL-2), and kill target cells. They are characterized by sustained expression of inhibitory receptors (e.g., PD-1, LAG-3, TIM-3) and distinct transcriptional and epigenetic changes. This state impairs effective immune responses against tumors or persistent pathogens.",
     "a simple caveman"
     "T cells are like strong hunters in the body, fighting enemies like cancer and viruses. If enemies stick around too long, T cells get tired and weak—they stop fighting well. This tiredness is called T cell exhaustion.",
     "an investor in biotech"
     "T cell exhaustion happens when immune cells called T cells get worn out from fighting cancer or infections for a long time. When they're exhausted, these T cells stop working as* well—they can’t kill cancer cells or infected cells effectively anymore. They become slow, lose their power, and don’t respond like they should.",
     "a cancer patient"
     "T cell exhaustion happens when your immune system’s T cells, which normally fight cancer or infections, get worn out from working too hard for too long. When this happens, the T cells become less effective at killing cancer cells and may stop working properly, making it harder for your body to fight the cancer."}}])

#_ (def roles (keys (:explanations (first elis))))

;;; Used in UI, first is default
(def roles
  ["an intelligent layman"
   "a five-year old"
   #_ "a high-school student"
   "a scientist in a different field"
   "an investor in biotech"
   "a cancer patient"])

(defn elit
  []
  (let [roles roles]
    [:div
     [:p "Demo of ELI (Explain Like I'm...) for different audiences. "]
    [:table.table.table-striped
     [:tbody
      [:tr
       [:th #_ "Explain ↓ to →" "Explain to"]
       (for [role roles] [:th role])]
      (for [{:keys [term explanations]} elis]
        [:tr
         [:th.align-top term]
         (for [role roles]
           [:td.align-top (get explanations role)])
         ])
      ]]]))
