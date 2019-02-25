import pandas as pd


def split_data():
    people = pd.read_csv("datasets/original/name.basics.tsv", delimiter="\t")
    people = people.dropna(how="any")
    print(people.shape)

    for i in range(8):
        people.iloc[i * 1000000:(i + 1) * 1000000, :].to_csv("datasets/tmp/names-{:d}.csv".format(i),
                                                             index=False, sep="\t")


def filter_professions():
    actors = []
    actresses = []
    directors = []

    for i in range(8):
        people = pd.read_csv("datasets/tmp/names-{:d}.csv".format(i), delimiter="\t")
        for index, person in people.iterrows():
            professions = person["primaryProfession"].split(",")
            if "director" in professions:
                directors.append(person)
            elif "actor" in professions:
                actors.append(person)
            elif "actress" in professions:
                actresses.append(person)

        print(i, "done")
    actors = pd.DataFrame(actors)
    actresses = pd.DataFrame(actresses)
    directors = pd.DataFrame(directors)
    actors.to_csv("datasets/tmp/actors.csv", index=False, sep="\t")
    actresses.to_csv("datasets/tmp/actresses.csv", index=False, sep="\t")
    directors.to_csv("datasets/tmp/directors.csv", index=False, sep="\t")


if __name__ == '__main__':
    # split_data()
    # filter_professions()
    pass



