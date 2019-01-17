# CRFVoter

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

