/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import java.io.Serializable;

/**
 *
 * @author user
 */
public class SubTable implements Serializable {
    
    public int id;
    public String title;
    public String status;

    public SubTable(int id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status; 
    }

    public String toString() {
        return "SubTable [id=" + id + ", title=" + title + ", status=" + status + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
