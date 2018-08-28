package com.team2502.ezauton.recorder;

public interface CSVAble {

    /**
     * Turn this object into a row (or rows) of csv output
     * @return Some valid CSV
     */
    String toCSV();
}
