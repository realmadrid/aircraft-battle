package edu.anu.comp6442.retrogame2018s1.model;

/*
 * Copyright (C) 2018,
 *
 * Jiewei Qian <u7472740@anu.edu.au>
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import android.content.Context;

/**
 * Scoreboard logic and file persistence
 *
 * Usage:
 *     Similar to standard List
 *
 * Creation:
 *     sb = new Scoreboard(appContext)
 *
 * Add score (lower scores are automatically dropped, keeps top 10 records)
 *     sb.add(score)
 *
 * Supports Iteration (for displaying score entries)
 *     for (Scoreboard.Record record: sb)
 *         doSomething(record)
 *
 * Conventional Get / Size
 *     sb.get(index)
 *     sb.size()
 *
 * Forget all records:
 *     sb.clear()
 *
 *  Write to file (required for persistence)
 *     sb.writeToFile()
 */

public class Scoreboard implements Serializable, Iterable<Scoreboard.Record> {
    // the number of records to keep
    static public int numberOfRecords = 30;

    public class Record implements Serializable {
        public int score;
        public String date;

        public Record(int score) {
            this.score = score;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            this.date = df.format(new Date());
        }
    }

    private class RecordComparator implements Comparator {
        @Override
        public int compare(Object a_, Object b_) {
            Record a = (Record) a_;
            Record b = (Record) b_;
            return b.score - a.score;
        }
    }

    private ArrayList<Record> list;
    private File file;

    public Scoreboard(Context ctx) {
        this(ctx, "default_scoreboard");
    }

    /**
     * constructor with file name
     * useful for testing
     */
    public Scoreboard(Context ctx, String name) {
        file = new File(ctx.getFilesDir(), name);
        read();
    }

    /**
     * add score
     *   1. automatically check if score is high enough
     *   2. record current date
     *   3. remove low score if necessary
     */
    public void add(int score) {
        list.add(new Record(score));
        Collections.sort(list, new RecordComparator());
        dropLowestRecords();
    }

    public void writeToFile(){
        write();
    }

    /**
     * List style methods
     */

    public int size() {
        return list.size();
    }
    public Record get(int index) {
        return list.get(index);
    }
    public Iterator<Record> iterator() {
        return list.iterator();
    }

    /**
     * clear scoreboard, also deletes file
     */
    public void clear() {
        file.delete();
        read();
    }

    /**
     * remove records of lowest scores, until scoreboard has at most numOfRecords entries
     */
    private void dropLowestRecords() {
        while (list.size() > numberOfRecords)
            list.remove(numberOfRecords);
    }

    /**
     * read file into scoreboard
     * if file format is incorrect, proceed as if no record exists
     */
    private void read() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            list = (ArrayList<Record>) ois.readObject();
            dropLowestRecords();
            ois.close();
        } catch(Exception e) {
            list = new ArrayList<Record>();
        }
    }

    /**
     * write current state into file
     */
    private void write() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(list);
            oos.close();
        } catch(Exception e) {
            // nop
        }
    }
}
