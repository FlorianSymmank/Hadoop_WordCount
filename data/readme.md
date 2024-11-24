This directory contains the data for the project.

Source: 
- https://www.dropbox.com/scl/fi/bajoqbxtz69sms9ny2kvh/texts.zip?rlkey=6rl8yaxu58kv319xu1genl930&e=1&dl=0

To combine the data into a single file, 1 GB in size, per language, run the following commands:
- Create python environment and install the required packages. (`pip install -r requirements.txt`)
- Run the `combine_data.py` script to combine the data into a single file per language.
- The combined data is saved in the corresponding language directory as `<language>.txt` file.