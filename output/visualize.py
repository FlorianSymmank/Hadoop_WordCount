import os
import matplotlib.pyplot as plt
import numpy as np
from wordcloud import WordCloud


def plot_top_10():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    print(dirs)
    for subdir in dirs:
        lang = subdir.split('_')[0]
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            lines = f.readlines()[:10]
            y = [int(line.strip().split('\t')[0]) for line in lines]
            x = [line.strip().split('\t')[1] for line in lines]
            plt.bar(x, y)
            plt.title(f'{lang}_sorted')
            plt.xticks(rotation=45)
            plt.tight_layout()
            plt.savefig(f'{lang}_sorted_top10.png')
            plt.close()


def plot_zipf():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        lang = subdir.split('_')[0]
        counts = []
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                counts.append(int(count))

        ranks = np.arange(1, len(counts) + 1)
        plt.figure(figsize=(10, 6))
        plt.loglog(ranks, counts, marker=".")
        plt.title(f'Zipf Plot for {lang}')
        plt.xlabel('Rank of the word')
        plt.ylabel('Frequency of the word')
        plt.grid(True, which="both", ls="--")
        plt.tight_layout()
        plt.savefig(f'{lang}_zipf_plot.png')
        plt.close()


def create_wordcloud():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        lang = subdir.split('_')[0]
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
        plt.title(f'Word Cloud for {lang}', fontsize=24)
        plt.tight_layout(pad=0)
        plt.savefig(f'{lang}_wordcloud.png')
        plt.close()


def plot_frequency_histogram():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        lang = subdir.split('_')[0]
        counts = []
        with open(f'{subdir}/part-r-00000', 'r', encoding="UTF-8") as f:
            for line in f:
                count, word = line.strip().split('\t')
                counts.append(int(count))

        plt.figure(figsize=(10, 6))
        plt.hist(counts, bins=100, log=True)
        plt.title(f'Word Frequency Histogram for {lang}')
        plt.xlabel('Frequency of the word')
        plt.ylabel('Number of words')
        plt.tight_layout()
        plt.savefig(f'{lang}_frequency_histogram.png')
        plt.close()


def plot_cdf():
    dirs = [d for d in os.listdir('.') if os.path.isdir(
        d) and d.endswith('_sorted')]

    for subdir in dirs:
        lang = subdir.split('_')[0]
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
        plt.title(f'Cumulative Distribution Function for {lang}')
        plt.xlabel('Number of words')
        plt.ylabel('Cumulative frequency')
        plt.grid(True)
        plt.tight_layout()
        plt.savefig(f'{lang}_cdf_plot.png')
        plt.close()


plot_top_10()
plot_zipf()
create_wordcloud()
plot_frequency_histogram()
plot_cdf()
