package com.lifecompass.model;

public class Community {
    private String id;
    private String name;
    private int members;
    private String status;
    private String description;

    public Community() {
    }

    public Community(String id, String name, int members, String status, String description) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.status = status;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getMembers() { return members; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMembers(int members) { this.members = members; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
}