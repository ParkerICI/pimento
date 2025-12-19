(ns pimento.eli
  )


(def elis
  [{:term "MIBI"
    :explanations
    {"a five-year old"
     "Multiplexed Ion Beam Imaging is like having a super camera that uses tiny, invisible particles to see lots of different colors and shapes in a piece of tissue. This helps scientists see many things at once, like where all the different \"helpers\" in your body are hiding, especially the ones fighting sickness.",
     "a high-school student"
     "Multiplexed Ion Beam Imaging (MIBI) is a special way to take detailed pictures of cells or tissues. Scientists add different metal tags to antibodies, which are proteins that can stick to specific parts of a cell (like markers for cancer or immune cells). Then, they shoot a beam of ions (tiny particles) at the sample. This knocks the metal tags off, and a machine detects which metals are there and where they are. This lets scientists see many different types of cells, all at once, in one tissue section—almost like making a super-detailed map showing many cell features at the same time.",
     "an intelligent layman"
     "Multiplexed Ion Beam Imaging (MIBI) is a technique used to look at many different proteins in a tissue sample at the same time. Scientists attach unique metal tags to antibodies that stick to specific proteins in the tissue. Then, they use a focused ion beam—like a super-fine laser—to knock these metal tags off the tissue, little by little. As the metals are released, a special machine detects them, telling researchers which proteins are present and exactly where they are in the tissue. This helps scientists get a detailed map of many different cell types and molecules, all at once, in the same tissue sample.",
     ,
     "a scientist in a different field"
     "Multiplexed Ion Beam Imaging (MIBI) is a tissue imaging technique that uses metal-tagged antibodies to label many different proteins simultaneously on a tissue section. A primary ion beam (usually oxygen or cesium) is directed onto the sample, which causes the release (sputtering) of the metal tags. These metals are then measured and mapped using mass spectrometry, generating highly multiplexed spatial images of dozens of proteins at subcellular resolution. This allows for comprehensive analysis of the cellular composition and spatial organization within complex tissues, such as tumors."
    "a simple caveman"
    "MIBI is like special paint for the body. Doctors use it to see inside you. The paint goes into the blood, sticks to places that might be sick, like a tumor or infection. Then, doctor uses a magic eye (camera) to see where the paint glows. If it glows bright, there might be a problem there."
    }
    }
   {:term "sgRNA"
    :explanations
    {"a five-year old"
     "sgRNA is like a little map that helps special scissors (called CRISPR) find the exact spot in a cell's DNA where they need to cut. It tells the scissors, \"Cut right here!\"",
     "a high-school student"
     "sgRNA stands for “single guide RNA.” It’s a short piece of RNA used in CRISPR gene editing. The sgRNA has two jobs: one part matches the DNA sequence you want to target, and the other part helps bring the Cas9 protein (the “molecular scissors”) to that exact spot in the DNA. Once they’re together, Cas9 can cut the DNA at the chosen location.",
     "an intelligent layman"
     "sgRNA stands for \"single guide RNA.\" It's a short piece of RNA used in CRISPR gene editing. Think of it as a GPS for the CRISPR system: it helps direct the editing machinery (Cas9 protein) to the exact spot in DNA that scientists want to cut or modify. The sgRNA matches up with the target DNA sequence, guiding Cas9 to the correct location so changes can be made accurately.",
     ,
     "a scientist in a different field"
     "sgRNA stands for \"single-guide RNA.\" It is a synthetic, engineered RNA molecule used in CRISPR-Cas9 gene editing systems. The sgRNA contains two components: a sequence (~20 nucleotides) that is complementary to a specific DNA target (the \"guide\" sequence), and a scaffold region that binds and directs the Cas9 endonuclease to the DNA. The sgRNA thus determines the specificity of where Cas9 will introduce a double-strand break in the genome."
     "a simple caveman"
"sgRNA is like a map that tells a cell’s cutting tool exactly where to go. It guides the tool to a spot in DNA, so it knows where to make a cut."
     }}

   {:term "T cell exhaustion"
    :explanations
    {"a five-year old"
     "T cell exhaustion is when the body’s fighter cells (T cells) get very tired because they have been fighting germs or cancer for too long. When they’re tired, they can’t fight as well anymore, like when you get tired after playing all day and need a rest.",
     "a high-school student"
     "T cell exhaustion happens when T cells—important immune cells that help fight infections and cancer—get overworked for a long time. Instead of staying strong, they become tired, weak, and stop working well. They don’t attack harmful cells as effectively, which can make it harder for the body to clear infections or fight cancer.",
     "an intelligent layman"
     "T cell exhaustion is when immune cells called T cells, which usually attack infections or cancer, become worn out after fighting for too long. When this happens, they stop working well—they don’t kill cancer cells or viruses effectively and show signs like having special “brake” proteins on their surface. This often happens during long battles with cancer or chronic infections, making it harder for the body to win.",
     ,
     "a scientist in a different field"
     "T cell exhaustion refers to a dysfunctional state that T cells enter after prolonged exposure to antigens, such as during chronic infections or cancer. Exhausted T cells have reduced ability to proliferate, secrete cytokines (like IFN-γ, TNF-α, and IL-2), and kill target cells. They are characterized by sustained expression of inhibitory receptors (e.g., PD-1, LAG-3, TIM-3) and distinct transcriptional and epigenetic changes. This state impairs effective immune responses against tumors or persistent pathogens."
     "a simple caveman"
     "T cells are like strong hunters in the body, fighting enemies like cancer and viruses. If enemies stick around too long, T cells get tired and weak—they stop fighting well. This tiredness is called T cell exhaustion." 
     }
    }

   ]
  )

(defn elit
  []
  (let [roles (keys (:explanations (first elis)))]
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
      ]]))
