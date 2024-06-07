package com.ivote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public class VotingService {
    
    protected IVoteDatabase db;
    protected List<ObjectId> qids;
    protected List<ObjectId> sids;

    protected boolean loggedIn;
    protected boolean adminLoggedIn;
    protected Student user;
    protected String userid;

    protected int questionPointer;

    public VotingService(String uri, String dbName, String questionsCollection, String studentsCollection) {
        this.db = new IVoteDatabase(uri, dbName, questionsCollection, studentsCollection);
        qids = new ArrayList<>();
        sids = new ArrayList<>();
        loggedIn = false;
        adminLoggedIn = false;
        user = null;
        userid = null;
    }

    public VotingService(String uri, String dbName) {
        this.db = new IVoteDatabase(uri, dbName, "ivotequestions", "ivotevoters");
        loggedIn = false;
        adminLoggedIn = false;
        user = null;
        userid = null;
    }

    public VotingService() {
        this.db = new IVoteDatabase("mongodb://localhost:27017", "ivotedatabase", "ivotequestions", "ivotevoters");
        loggedIn = false;
        adminLoggedIn = false;
        user = null;
        userid = null;
    }

    public boolean addStudent(String name) {
        ObjectId id = this.db.addStudent(name);
        if (id != null) {
            return sids.add(id);
        }
        return false;
    }

    public boolean addQuestion(String text, List<String> answers) {
        ObjectId id = this.db.addQuestion(text, answers);
        if (id != null) {
            return qids.add(id);
        }
        return false;
    }

    public boolean addStudents(List<String> names) {
        for (String name : names) {
            if (!addStudent(name)) return false;
        }
        return true;
    }

    public boolean addQuestions(Map<String, List<String>> questions) {
        for (Map.Entry<String, List<String>> entry : questions.entrySet()) {
            String text = entry.getKey();
            List<String> answers = entry.getValue();
            if (!addQuestion(text, answers)) return false;
        }
        return true;
    }

    public boolean loginAsAdmin() {
        loggedIn = true;
        adminLoggedIn = true;
        user = null;
        return true;
    }

    public boolean loginAsStudent(String sid) {
        user = db.getStudent(new ObjectId(sid));
        if (user == null) return false;

        userid = sid;
        loggedIn = true;
        adminLoggedIn = false;

        return true;
    }

    public boolean loginAsNewStudent(String name) {
        String sid = createStudentAccount(name);
        return loginAsStudent(sid);
    }

    public String createStudentAccount(String name) {
        return db.addStudent(name).toString();
    }

    public boolean isAdmin() {
        return adminLoggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}
