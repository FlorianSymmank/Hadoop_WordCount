import json

translation = {
    "Input File": "input_file",
    "Input File Size (bytes)": "input_file_size",
    "Language": "language",
    "Total Words": "total_words",
    "Elapsed Time (ms)": "elapsed_time",
    "Words per Minute": "words_per_minute",
    "Total Keys": "total_keys",
    "Keys per Minute": "keys_per_minute",
    "Output File": "output_file",
}

file_path = 'res.txt'

result_count = {}
result_sort = {}


with open(file_path, 'r', encoding='utf-8') as file:
    lines = file.readlines()

curr = {}
for line in lines:
    parts = line.split(":")
    
    if line == "\n":
        res_key = curr.get("output_file", curr["input_file"])
        res_key = res_key.replace(".txt", "")

        if "output_file" in curr:
            curr["language"] = curr["output_file"].split("_")[0]
            result_sort[res_key] = curr
        else:
            if curr["language"] == "xx":
                curr["language"] = res_key.split("_")[0]
                res_key += "_without_stopwords"

            result_count[res_key] = curr

        curr = {}
    else:
        curr_key = translation[parts[0].strip()]
        value = parts[1].strip()
        curr[curr_key] = value

## get the last one
res_key = curr.get("output_file", curr["input_file"])
res_key = res_key.replace(".txt", "")

if "output_file" in curr:
    curr["language"] = curr["output_file"].split("_")[0]
    result_sort[res_key] = curr
else:
    if curr["language"] == "xx":
        res_key += "_xx"

    result_count[res_key] = curr
    
with open("res_count.json", 'w', encoding='utf-8') as json_file:
    json.dump(result_count, json_file, ensure_ascii=False, indent=4)

with open("res_sort.json", 'w', encoding='utf-8') as json_file:
    json.dump(result_sort, json_file, ensure_ascii=False, indent=4)