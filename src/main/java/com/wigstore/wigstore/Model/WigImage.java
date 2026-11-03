package com.wigstore.wigstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wig_images")
public class WigImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // only the generated file name is stored; the full URL is built in the service
    @Column(nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private boolean primaryImage = false;

    @Column(nullable = false)
    private int displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wig_id", nullable = false)
    private Wig wig;

    public Long getId() { return id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public boolean isPrimaryImage() { return primaryImage; }
    public void setPrimaryImage(boolean primaryImage) { this.primaryImage = primaryImage; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public Wig getWig() { return wig; }
    public void setWig(Wig wig) { this.wig = wig; }
}