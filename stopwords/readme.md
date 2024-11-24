This directory contains the stopwords for the project.

The stopwords are used to remove the common words from the text data. This is done to reduce the noise in the data.

Sources:
- https://github.com/6/stopwords-json
- https://github.com/Alir3z4/stop-words
- https://github.com/stopwords-iso

To combine the stopwords from the above sources, the following steps were followed:
- Download the stopwords from the above sources.
- Create python environment and install the required packages. (`pip install -r requirements.txt`)
- Run the `combine.py` script to combine the stopwords from the above sources.
- The combined stopwords are saved in the `stopwords.json` file.