package com.example.sneakerfinder.data;

import androidx.annotation.NonNull;

/**
 * Represents one single result of the shoe classification. It contains a className (the shoes name)
 * an accuracy as probability between 0% and 100% and an internal classId.
 */
public class ClassificationResult implements Comparable<ClassificationResult> {
    private final int classId;
    private final String className;
    private final float accuracy;

    public ClassificationResult(int classId, String className, float accuracy) {
        this.classId = classId;
        this.className = className;
        this.accuracy = accuracy;
    }

    @NonNull
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
