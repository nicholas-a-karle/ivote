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
    protected Question question;

    protected int questionPointer;

    public VotingService(String uri, String dbName, String questionsCollection, String studentsCollection) {
        this.db = new IVoteDatabase(uri, dbName, questionsCollection, studentsCollection);
        qids = new ArrayList<>();
        sids = new ArrayList<>();
        loggedIn = false;
        adminLoggedIn = false;
        user = null;
    }

    public VotingService(String uri, String dbName) {
        this.db = new IVoteDatabase(uri, dbName, "ivotequestions", "ivotevoters");
        qids = new ArrayList<>();
        sids = new ArrayList<>();
        loggedIn = false;
        adminLoggedIn = false;
        user = null;
    }

    public VotingService() {
        this.db = new IVoteDatabase("mongodb://localhost:27017", "ivotedatabase", "ivotequestions", "ivotevoters");
        qids = new ArrayList<>();
        sids = new ArrayList<>();
        loggedIn = false;
        adminLoggedIn = false;
        user = null;
    }

    public String getStateReport() {
        String str = db.getDatabaseState();

        str += "NUMERICALS________\n";
        str += "Num Students: " + getNumStudents() + "\n";
        str += "Num Questions: " + getNumQuestions() + "\n";

        return str;
    }

    public String addStudent(String name) {
        ObjectId id = this.db.addStudent(name);
        if (id != null) {
            if (!sids.add(id)) return null;
            return id.toString();
        }
        return null;
    }

    public String addQuestion(String text, List<String> answers) {
        ObjectId id = this.db.addQuestion(text, answers);
        if (id != null) {
            if (!qids.add(id)) return null;
            return id.toString();
        }
        return null;
    }

    public List<String> addStudents(List<String> names) {
        List<String> ids = new ArrayList<>();
        for (String name : names) {
            ids.add(addStudent(name));
        }
        return ids;
    }

    public List<String> addQuestions(Map<String, List<String>> questions) {
        List<String> ids = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : questions.entrySet()) {
            String text = entry.getKey();
            List<String> answers = entry.getValue();
            ids.add(addQuestion(text, answers));
        }
        return ids;
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

        loggedIn = true;
        adminLoggedIn = false;

        return true;
    }

    public List<Student> getStudents(int from, int to) {
        List<ObjectId> ids = db.getAllStudentIds();
        List<Student> students = new ArrayList<>();
        for (int i = from; i < to && i < getNumStudents(); ++i) {
            students.add(db.getStudent(ids.get(i)));
        }
        return students;
    }

    public String getQuestionDisplayString() {
        String str = question.toDisplayString();

        return str;
    }

    public void vote(List<Integer> answer) {
        db.addStudentAnswer(user.getId(), question.getId(), answer);
    }

    public int getNumStudents() {
        return db.getNumStudents();
    }

    public int getNumQuestions() {
        return db.getNumQuestions();
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

    public String getUserName() {
        return user.getName();
    }

    public String getUserDisplayString() {
        return user.toFullDisplayString();
    }

    public void interpretVoterInput(String input) {
        String[] tokens = input.split(",");
        // Create an ArrayList to store the integers
        List<Integer> votes = new ArrayList<>();

        try {
            // Try to parse each token as an integer and add to the list
            for (String token : tokens) {
                // Trim any whitespace around the token
                token = token.trim();
                
                // Check if the token is empty (in case of input like "1,,2")
                if (!token.isEmpty()) {
                    int vote = Integer.parseInt(token);
                    votes.add(vote);
                }
            }
            if (!votes.isEmpty()) {
                vote(votes);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, do nothing
            System.out.println("Invalid input: " + input);
        }
    }

}
