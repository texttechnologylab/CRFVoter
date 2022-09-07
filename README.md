[![version](https://img.shields.io/github/license/texttechnologylab/CRFVoter)]()
[![](https://jitpack.io/v/texttechnologylab/CRFVoter.svg)](https://jitpack.io/#texttechnologylab/CRFVoter)
[![Paper](http://img.shields.io/badge/paper-Journal_of_Cheminformatics-B31B1B.svg)](https://jcheminf.biomedcentral.com/track/pdf/10.1186/s13321-019-0343-x.pdf)

# CRFVoter

## Abstract
Gene and protein related objects are an important class of entities in biomedical research, whose identification and extraction from scientific articles is attracting increasing interest. In this work, we describe an approach to the BioCreative V.5 challenge regarding the recognition and classification of gene and protein related objects. For this purpose, we transform the task as posed by BioCreative V.5 into a sequence labeling problem. We present a series of sequence labeling systems that we used and adapted in our experiments for solving this task. Our experiments show how to optimize the hyperparameters of the classifiers involved. To this end, we utilize various algorithms for hyperparameter optimization. Finally, we present CRFVoter, a two-stage application of Conditional Random Field (CRF) that integrates the optimized sequence labelers from our study into one ensemble classifier.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
In order to run the CRFVoter you need
* Java 8
* Maven

### Installing
Clone and star this repository
```
git clone https://github.com/texttechnologylab/CRFVoter
```
Navigate to the directory and build project
```
cd ../some/dir/CRFVoter
mvn install
```

### Running

#### CLI API
After ```mvn install``` the CLI script will be generated in ```target/CRFVoter.jar```.

Instructions and help will be printed with:  ```java -jar target/CRFVoter.jar -h```

Example client call:
```
cd ../some/dir/CRFVoter/target
java -jar CRFVoter.jar -i "The PAR4 antagonist can be combined with a PAR2 antagonist."
```

The above example will parse the input document defined in ```-i``` and print the output in the commandline in the CONLL format.


### TODO
- [ ] Documentation
- [x] Add input/output support for different formats
- [x] CLI
- [x] Support for Windows


# Cite
If you want to use the project please quote this as follows:

W. Hemati and A. Mehler, “CRFVoter: gene and protein related object recognition using a conglomerate of CRF-based tools,” Journal of Cheminformatics, vol. 11, iss. 1, p. 11, 2019.  ![[Link]](https://doi.org/10.1186/s13321-019-0343-x) ![[PDF]](https://jcheminf.biomedcentral.com/track/pdf/10.1186/s13321-019-0343-x.pdf)
 
## BibTeX
```
@article{Hemati:Mehler:2019b,
  author="Hemati, Wahed and Mehler, Alexander",
  title="{{CRFVoter}: gene and protein related object recognition using a conglomerate of CRF-based tools}",
  journal={Journal of Cheminformatics},
  year="2019",
  month="Mar",
  day="14",
  volume="11",
  number="1",
  pages="11",
  issn="1758-2946",
  doi="10.1186/s13321-019-0343-x",
  url="https://doi.org/10.1186/s13321-019-0343-x"
}
```
