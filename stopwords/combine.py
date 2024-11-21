import iso639
import json
import os.path
from collections import defaultdict

# read stopwords from stopwords-all.json
stopwords_all = {}
with open('stopwords-all.json', 'r', encoding="UTF-8") as file:
    stopwords_all = json.load(file)

# print(len(stopwords_all.get("af", [])))


# read stopwords from stopwords-iso.json
stopwords_iso = {}
with open('stopwords-iso.json', 'r', encoding="UTF-8") as file:
    stopwords_iso = json.load(file)

# print(len(stopwords_iso.get("af", [])))


# read stopwords from loose files
loose_files = {}
for lang in iso639.ALL_LANGUAGES:
    file_name = f"{lang.name.lower()}.txt"
    if os.path.isfile(file_name):
        with open(file_name, "r", encoding="UTF-8") as file:
            loose_files[lang.part1] = file.read().split("\n")

# print(len(loose_files.get("af", [])))


# combine all stopwords
dicts = [stopwords_all, stopwords_iso, loose_files]

combined = defaultdict(list)
for d in dicts:
    for k, v in d.items():
        combined[k].extend(v)

# print(len(combined.get("af", [])))


# remove duplicates and empty strings, sort
for k, v in combined.items():
    combined[k] = list(set(v))
    combined[k] = [x for x in combined[k] if x]
    combined[k].sort()

# print(len(combined.get("af", [])))


# write to file
with open('stopwords-combined.json', 'w', encoding="UTF-8") as file:
    json.dump(combined, file, ensure_ascii=False, indent=4)