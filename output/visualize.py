import os
import matplotlib.pyplot as plt
import numpy as np
from wordcloud import WordCloud
import json


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


def calculate_correlation(json_data, key1, key2):
    values1 = np.array([int(value[key1]) for value in json_data.values()])
    values2 = np.array([int(value[key2]) for value in json_data.values()])

    correlation_coefficient = np.corrcoef(values1, values2)[0, 1]
    return correlation_coefficient


def plot_meta(file_path, title, key_x: tuple[str, str], key_y: tuple[str, str]):
    with open(file_path, 'r') as file:
        json_data = json.load(file)

    x_values = [int(value[key_x[0]]) 
               for value in json_data.values()]
    y_values = [int(value[key_y[0]])
                   for value in json_data.values()]

    correlation_coefficient = calculate_correlation(
        json_data, key_x[0], key_y[0])

    # Plotting
    plt.figure(figsize=(10, 5))
    plt.plot(x_values, y_values, 'o')

    # coefficients = np.polyfit(keyx_values, keyy_values, 1)
    # polynomial = np.poly1d(coefficients)
    # trendline = polynomial(np.array(keyy_values))
    # plt.plot(keyy_values, trendline, color='red', label='Trendline')

    plt.title(f'{key_x[1]} by {key_y[1]} ({title})')
    # plt.suptitle(f"The correlation coefficient is {
    #              correlation_coefficient}", fontsize=10)
    plt.xlabel(key_x[1])
    plt.ylabel(key_y[1])

    plt.grid()
    # plt.legend()
    plt.show()

print("Visualizing the output")

print("Top 10 words")
plot_top_10()

print("Zipf plot")
plot_zipf()

print("Word Cloud")
create_wordcloud()

print("Frequency Histogram")
plot_frequency_histogram()

print("CDF")
plot_cdf()

print("Res files")
plot_meta('res_count.json', "Count", ("elapsed_time",
          "elapsed_time"), ("words_per_minute", "words_per_minute"))

