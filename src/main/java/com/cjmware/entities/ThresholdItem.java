package com.cjmware.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name="threshold_items")
@Entity
public class ThresholdItem {

    @Id
    @GeneratedValue()
    public Integer ID;

    public Date batch_date;

    public String batch_id;

    public String ip_address;

    public Long hits;

    public String message;

}
