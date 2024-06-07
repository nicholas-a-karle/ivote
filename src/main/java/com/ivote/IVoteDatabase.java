package com.ivote;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class IVoteDatabase extends Database {

    private String qc;
    private String sc;

    public IVoteDatabase(String uri, String dbName, String questionsCollection, String studentsCollection) {
        super(uri, dbName);
        createCollection(qc = questionsCollection);
        createCollection(sc = studentsCollection);
    }

    public ObjectId addQuestion(String text, List<String> answers) {
        Document question = new Document();
        
        question.append("text", text);
        question.append("answers", answers);

        this.setCurrentCollection(qc);
        return this.addDocument(question);
    }

    public ObjectId addStudent(String name) {
        Document student = new Document();

        student.append("name", name);
        
        Document answersDoc = new Document();

        student.append("answers", answersDoc);

        this.setCurrentCollection(sc);
        return this.addDocument(student);
    }

    public List<ObjectId> getAllStudentIds() {
        this.setCurrentCollection(sc);
        return this.getAllObjectIds();
    }

    public List<ObjectId> getAllQuestionIds() {
        this.setCurrentCollection(qc);
        return this.getAllObjectIds();
    }

    public Student getStudent(ObjectId sid) {
        this.setCurrentCollection(sc);
        Document sd = this.getDocument(sid);
        return new Student(sd.get("name").toString(), sid.toString());
    }

    public Question getQuestion(ObjectId qid) {
        this.setCurrentCollection(qc);
        Document qd = this.getDocument(qid);

        List<String> answers = new ArrayList<>();
        Document answersDoc = qd.get("answers", Document.class);
        if (answersDoc != null) {
            for (String key : answersDoc.keySet()) {
                String answer = answersDoc.getString(key);
                if (answer != null) {
                    answers.add(answer);
                }
            }
        }

        return new Question(qd.get("text").toString(), answers);
    }

    public boolean addStudentAnswer(ObjectId studentId, String questionId, int answer) {
        // checking questions collection
        MongoCollection<Document> collection = database.getCollection(qc);
        try {
            Document doc = collection.find(Filters.eq("_id", questionId)).first();
            if (doc == null) {
                return false;
            }
        } catch (MongoException e) {
            System.err.println("An error occurred while checking the ID: " + e.getMessage());
            return false;
        }

        // work in the student collection
        this.setCurrentCollection(sc);
        collection = database.getCollection(sc);

        try {
            collection.updateOne(
                Filters.eq("_id", studentId), 
                Updates.set("answers." + questionId, answer));
            System.out.println("Answer added/updated successfully for student: " + studentId);
        } catch (MongoException e) {
            System.err.println("An error occurred while updating the answer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean removeStudent (ObjectId sid) {
        this.setCurrentCollection(sc);
        return this.removeDocument(sid);
    }

    public boolean removeQuestion (ObjectId qid) {
        this.setCurrentCollection(qc);
        return this.removeDocument(qid);
    }

    
}
