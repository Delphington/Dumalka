package ru.myitschool.vsu2021.daimidzenko.com.dumalka.Panel.AboutApp.Src;

public class Versions {
    private String codeName;

    private String description;
    private boolean expandable;

    public Versions(String codeName, String description) {
        this.codeName = codeName;
        this.description = description;
        this.expandable = false;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Versions{" +
                "codeName='" + codeName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
