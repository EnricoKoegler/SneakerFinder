package com.example.sneakerfinder.ml;

public class ClassificationResult implements Comparable<ClassificationResult> {
    private int classId;
    private String className;
    private float accuracy;

    public ClassificationResult(int classId, String className, float accuracy) {
        this.classId = classId;
        this.className = className;
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "ClassificationResult{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", accuracy=" + accuracy +
                '}';
    }

    public int getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public float getAccuracy() {
        return accuracy;
    }

    @Override
    public int compareTo(ClassificationResult other) {
        return Float.compare(other.accuracy, this.accuracy);
    }
}
