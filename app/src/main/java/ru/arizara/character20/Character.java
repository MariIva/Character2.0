package ru.arizara.character20;

import java.io.Serializable;

public class Character implements Serializable {
    String img_uri;
    String name;
    int age;
    String sex;
    String race;
    Feature feature;

    public Character(String img_uri, String name, int age, String sex, String race, int str,
                     int dex, int con, int intl, int wis, int charm) {
        this.img_uri = img_uri;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.race = race;
        feature = new Feature(str, dex, con, intl, wis, charm);
    }

    class Feature  implements Serializable{
        int str;
        int dex;
        int con;
        int intl;
        int wis;
        int charm;

        public Feature(int str, int dex, int con, int intl, int wis, int charm) {
            this.str = str;
            this.dex = dex;
            this.con = con;
            this.intl = intl;
            this.wis = wis;
            this.charm = charm;
        }
    }
}
