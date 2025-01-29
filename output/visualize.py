import os
import matplotlib.pyplot as plt
import numpy as np
from wordcloud import WordCloud
import json
import math

def plot_top_10():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            lines = f.readlines()[:10]
            y = [int(line.strip().split('\t')[0]) for line in lines]
            x = [line.strip().split('\t')[1] for line in lines]
            plt.bar(x, y)
            plt.title(subdir)
            plt.xticks(rotation=45)
            plt.tight_layout()
            plt.savefig(f'{subdir}/sorted_top10.png')
            plt.close()

def plot_top_10_in_one():
    dirs = [d for d in os.listdir('.') if os.path.isdir(d) and d.endswith('_sorted') and "xx" not in d]
    num_dirs = len(dirs)
    
    cols = 4
    rows = math.ceil(num_dirs / cols)  # Calculate the number of rows needed
    
    fig, axs = plt.subplots(rows, cols, figsize=(15, 5 * rows))  # Create subplots
    axs = axs.flatten()  # Flatten the 2D array of axes for easy indexing
    
    for i, subdir in enumerate(dirs):
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            lines = f.readlines()[:10]
            y = [int(line.strip().split('\t')[0]) for line in lines]
            x = [line.strip().split('\t')[1] for line in lines]
            axs[i].bar(x, y)
            axs[i].set_title(subdir)
            axs[i].tick_params(axis='x', rotation=45)
    
    # Hide any empty subplots
    for j in range(i + 1, rows * cols):
        axs[j].axis('off')
    
    plt.tight_layout()
    plt.savefig('all_sorted_top10.png')  # Save the combined figure
    plt.close()


def plot_zipf():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        counts = []
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                counts.append(int(count))

        ranks = np.arange(1, len(counts) + 1)
        plt.figure(figsize=(10, 6))
        plt.loglog(ranks, counts, marker=".")
        plt.title(f'Zipf Plot for {subdir}')
        plt.xlabel('Rank of the word')
        plt.ylabel('Frequency of the word')
        plt.grid(True, which="both", ls="--")
        plt.tight_layout()
        plt.savefig(f'{subdir}/zipf_plot.png')
        plt.close()


def create_wordcloud():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        word_freq = {}
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                word_freq[word] = int(count)

        wordcloud = WordCloud(width=1600, height=800, background_color='white',
                              max_words=500).generate_from_frequencies(word_freq)

        plt.figure(figsize=(20, 10))
        plt.imshow(wordcloud, interpolation='bilinear')
        plt.axis('off')
        plt.title(f'Word Cloud for {subdir}', fontsize=24)
        plt.tight_layout(pad=0)
        plt.savefig(f'{subdir}/wordcloud.png')
        plt.close()


def plot_frequency_histogram():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        counts = []
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                counts.append(int(count))

        plt.figure(figsize=(10, 6))
        plt.hist(counts, bins=100, log=True)
        plt.title(f'Word Frequency Histogram for {subdir}')
        plt.xlabel('Frequency of the word')
        plt.ylabel('Number of words')
        plt.tight_layout()
        plt.savefig(f'{subdir}/frequency_histogram.png')
        plt.close()

def plot_frequency_histogram_in_one():
    dirs = [d for d in os.listdir('.') if os.path.isdir(d) and d.endswith('_sorted') and "xx" in d]
    
    num_plots = len(dirs)
    cols = 4
    rows = (num_plots + cols - 1) // cols  # Calculate number of rows needed

    plt.figure(figsize=(15, 5 * rows))  # Adjust figure size based on number of rows

    for i, subdir in enumerate(dirs):
        counts = []
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                counts.append(int(count))

        plt.subplot(rows, cols, i + 1)  # Create a subplot in the grid
        plt.hist(counts, bins=100, log=True)
        plt.title(subdir)
        plt.xlabel('Frequency of the word')
        plt.ylabel('Number of words')

    plt.tight_layout()
    plt.savefig('combined_frequency_histograms_xx.png')  # Save all plots in one image
    plt.close()

def plot_cdf():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        counts = []
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                counts.append(int(count))

        counts = np.array(counts)
        counts_sorted = np.sort(counts)[::-1]
        cdf = np.cumsum(counts_sorted) / np.sum(counts_sorted)

        plt.figure(figsize=(10, 6))
        plt.plot(cdf)
        plt.title(f'Cumulative Distribution Function for {subdir}')
        plt.xlabel('Number of words')
        plt.ylabel('Cumulative frequency')
        plt.grid(True)
        plt.tight_layout()
        plt.savefig(f'{subdir}/cdf_plot.png')
        plt.close()


def plot_total_words_w_and_wo_stopwords():
    with open('res_count.json', 'r') as file:
        data = json.load(file)

    # Prepare data for plotting
    languages = ['de', 'en', 'es', 'fr', 'it', 'nl', 'ru', 'uk']
    total_words_with_stopwords = []
    total_words_without_stopwords = []

    for lang in languages:
        total_words_with_stopwords.append(int(data.get(f"{lang}_all", {}).get('total_words', 0)))
        total_words_without_stopwords.append(int(data.get(f"{lang}_all_without_stopwords", {}).get('total_words', 0)))

    # Define bar positions
    x = range(len(languages))
    width = 0.4  # Width of the bars

    # Side by side bar chart
    plt.bar([i - width/2 for i in x], total_words_without_stopwords, width=width, label='With Stopwords', color='lightblue')
    plt.bar([i + width/2 for i in x], total_words_with_stopwords, width=width, label='Without Stopwords', color='blue')

    # Calculate and annotate ratios
    for i in x:
        if total_words_without_stopwords[i] > 0:  # Avoid division by zero
            ratio = total_words_with_stopwords[i] / total_words_without_stopwords[i]
            plt.text(i, max(total_words_without_stopwords[i], total_words_with_stopwords[i]) + 10000, 
                     f'{ratio:.2f}', ha='center', va='bottom')  # Adjusted position for the ratio

    plt.xticks(x, languages)
    plt.ylabel('Total Words')
    plt.title('Total Words with and without Stopwords by Language')
    plt.legend()
    plt.tight_layout()
    plt.savefig(f'tw_w_wo_stopwords.png')
    plt.close()


def plot_total_words_by_elapsed_time():
    with open('res_count.json', 'r') as file:
        data = json.load(file)

    # Prepare data for plotting
    languages = []
    total_words = []
    elapsed_time = []

    for key, value in data.items():
        languages.append(key)
        total_words.append(int(value['total_words']))
        elapsed_time.append(int(value['elapsed_time']))

    # Create a scatter plot for total words vs. elapsed time
    plt.figure(figsize=(10, 6))
    plt.scatter(elapsed_time, total_words, color='blue', label='Data Points')

    # Fit a quadratic polynomial to the data
    coefficients = np.polyfit(elapsed_time, total_words, 1)
    polynomial = np.poly1d(coefficients)

    # Create a range of x values for plotting the trendline
    x_values = np.linspace(min(elapsed_time), max(elapsed_time), 100)
    y_values = polynomial(x_values)

    # Plot the trendline
    plt.plot(x_values, y_values, color='red', label='Trendline')

    # Annotate each point with the language key
    for i, lang in enumerate(languages):
        plt.annotate(lang, (elapsed_time[i], total_words[i]), textcoords="offset points", xytext=(0,5), ha='center')

    plt.xlabel('Elapsed Time (ms)')
    plt.ylabel('Total Words')
    plt.title('Total Words by Elapsed Time with Trendline')
    plt.grid()
    plt.legend()
    plt.tight_layout()
    plt.savefig(f'tw_by_elapsed_time.png')
    plt.close()

def plot_total_keys_by_elapsed_time():
    with open('res_sort.json', 'r') as file:
        data = json.load(file)

    # Prepare data for plotting
    languages = []
    total_words = []
    elapsed_time = []

    for key, value in data.items():
        languages.append(key)
        total_words.append(int(value['total_keys']))
        elapsed_time.append(int(value['elapsed_time']))

    # Create a scatter plot for total keys vs. elapsed time
    plt.figure(figsize=(10, 6))
    plt.scatter(elapsed_time, total_words, color='blue', label='Data Points')

    # Fit a quadratic polynomial to the data
    coefficients = np.polyfit(elapsed_time, total_words, 1)
    polynomial = np.poly1d(coefficients)

    # Create a range of x values for plotting the trendline
    x_values = np.linspace(min(elapsed_time), max(elapsed_time), 100)
    y_values = polynomial(x_values)

    # Plot the trendline
    plt.plot(x_values, y_values, color='red', label='Trendline')

    # Annotate each point with the language key
    for i, lang in enumerate(languages):
        plt.annotate(lang, (elapsed_time[i], total_words[i]), textcoords="offset points", xytext=(0,5), ha='center')

    plt.xlabel('Elapsed Time (ms)')
    plt.ylabel('Total Keys')
    plt.title('Total Keys by Elapsed Time with Trendline')
    plt.grid()
    plt.legend()
    plt.tight_layout()
    plt.savefig(f'tk_by_elapsed_time.png')
    plt.close()

def plot_runtimes():
    # load all files in dir with name like 'stats_*.json', use filename as series name and plot x value in file as "input size" and y value as "elapsed time"
    files = [f for f in os.listdir('.') if os.path.isfile(f) and f.startswith('stats_') and f.endswith('.json')]

    for file in files:
        with open(file, 'r') as f:
            data = json.load(f)
            y = [d['x'] for d in data] # input size
            x = [d['y'] for d in data] # elapsed time

            # scale y to GB
            y = [value / 1_073_741_824 for value in y]
            # scale x to ms
            x = [value / 1000 for value in x]
            plt.plot(y, x, label=file.replace('.json', '').replace('stats_', ''))

    plt.xlabel('Input Size (GB)')
    plt.ylabel('Elapsed Time (sek)')
    plt.title('Runtimes by Input Size')
    plt.legend()
    plt.tight_layout()
    plt.savefig(f'runtimes.png')
    plt.close()


# print("Visualizing the output")

# print("Top 10 words")
# plot_top_10()
# plot_top_10_in_one()

# print("Zipf plot")
# plot_zipf()

# print("Word Cloud")
# create_wordcloud()

# print("Frequency Histogram")
# plot_frequency_histogram()
# plot_frequency_histogram_in_one()

# print("CDF")
# plot_cdf()

# print("Total Words with and without Stopwords by Language")
# plot_total_words_w_and_wo_stopwords()
# plot_total_words_by_elapsed_time()
# plot_total_keys_by_elapsed_time()

print("Runtimes")
plot_runtimes()
