import pandas as pd
from random import random


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


def filter_num_of_titles():
    dsets = ["directors", "actresses", "actors"]
    for dataset in dsets:
        people = pd.read_csv("datasets/tmp/{}.csv".format(dataset), delimiter="\t")
        filtered = []
        for index, person in people.iterrows():
            if len(person["knownForTitles"].split(",")) > 1:
                filtered.append(person)
        result = pd.DataFrame(filtered)
        result.to_csv("datasets/tmp/{}_filtered.csv".format(dataset), index=False, sep="\t")


def filter_films():
    films = pd.read_csv("datasets/original/title.basic.tsv", delimiter="\t")
    films = films.dropna(how="any")
    films = films.drop(["titleType", "originalTitle", "isAdult", "endYear", "runtimeMinutes", "genres"], axis=1)
    films.to_csv("datasets/tmp/films_filtered.csv", index=False, sep="\t")


def create_source_dataset():
    directors = pd.read_csv("datasets/tmp/directors_filtered.csv", delimiter="\t")
    films = pd.read_csv("datasets/tmp/films_filtered.csv", delimiter="\t")
    actors = pd.read_csv("datasets/tmp/actors_filtered.csv", delimiter="\t")
    actresses = pd.read_csv("datasets/tmp/actresses_filtered.csv", delimiter="\t")
    with open("datasets/final/source.csv", 'a') as output:
        names = ["Sergio Leone", "Akira Kurosawa", "Steven Spielberg", "George Lucas"]  # and so on...
        for name in names:
            for i_dir, director in directors.loc[directors['primaryName'] == name].iterrows():
                print(director)
                found_films = films.loc[films['tconst'].isin(
                    directors.loc[directors['primaryName'] == name].iloc[0]["knownForTitles"].split(","))]
                for i_film, film in found_films.iterrows():
                    output.write(film["primaryTitle"] + ";")
                    output.write(director["primaryName"] + ";")
                    output.write(film["startYear"] + ";")
                    for i_actor, actor in actors.iterrows():
                        if film['tconst'] in actor["knownForTitles"].split(","):
                            output.write(actor["primaryName"] + ";")
                            break
                    for i_actress, actress in actresses.iterrows():
                        if film['tconst'] in actress["knownForTitles"].split(","):
                            output.write(actress["primaryName"])
                            break
                    output.write("\n")


def modify_first_knowledgebase():
    with open("datasets/final/kb_1.csv", 'w') as output:
        output.write("title;director;productionYear;actor;actress\n")
        source = pd.read_csv("datasets/final/source.csv", delimiter=";")
        for index, film in source.iterrows():
            output.write(film["title"] + ";")
            if random() < 0.85:
                output.write(film["director"])
            output.write(";")
            if random() < 0.85:
                output.write(str(film["year"]))
            output.write(";")
            if random() < 0.85:
                output.write(film["actor"])
            output.write(";")
            output.write(film["actress"] + "\n")


def modify_second_knowledgebase():
    with open("datasets/final/kb_2.csv", 'a') as output:
        source = pd.read_csv("datasets/final/source.csv", delimiter=";")
        for index, film in source.iterrows():
            output.write(film["title"] + ";")
            director = film["director"].split(" ")
            output.write(director[0][0] + ". " + director[-1] + ";")
            output.write(str(film["year"]) + ";")
            actor = film["actor"].split(" ")
            output.write(actor[0][0] + ". " + actor[-1] + ";")
            actress = film["actress"].split(" ")
            output.write(actress[0][0] + ". " + actress[-1] + "\n")


if __name__ == '__main__':
    # split_data()
    # filter_professions()
    # filter_num_of_titles()
    # filter_films()
    # create_source_dataset()
    modify_first_knowledgebase()
    # modify_second_knowledgebase()









