import os
from tqdm import tqdm

# Define the target size in bytes (1 GB)
TARGET_SIZE = 1 * 1024 * 1024 * 1024

# Get all subdirectories of the current directory
dirs = [d for d in os.listdir('.') if os.path.isdir(
    d) and not d.startswith('.') and not d.startswith("Test")]

# Combine all files in each subdirectory until the result file is 1 GB
for idx, d in enumerate(tqdm(dirs, desc="Processing directories", position=0)):
    # Exclude the output file
    files = [f for f in os.listdir(d) if not f.startswith(d)]
    path = os.path.join(d, f"{d}.txt")
    total_size = 0

    with open(path, 'w', encoding="UTF-8") as outfile:
        with tqdm(total=TARGET_SIZE, unit='B', unit_scale=True, desc=f"Building {d}.txt", position=1, leave=False) as pbar:
            while total_size <= TARGET_SIZE:
                for fname in files:
                    with open(os.path.join(d, fname), encoding="UTF-8") as infile:
                        content = infile.read()
                        encoded_content = content.encode('utf-8')
                        content_size = len(encoded_content)
                        total_size += content_size
                        pbar.update(content_size)
                        outfile.write(content)
                        if total_size >= TARGET_SIZE:
                            break
                else:
                    continue
                break
