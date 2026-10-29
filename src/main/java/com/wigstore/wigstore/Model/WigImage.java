package com.wigstore.wigstore.model;
import jakarta.persistence.*;

@Entity
@Table(name= "wig_images")
public class WigImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false, unique= true)
    private String imageUrl;

    @Column(nullable= false)
    private boolean primaryImage= false;

    @Column(nullable= false)
    private int displayOrder=0;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "wig_id")
    private Wig wig;

    public Long getId(){return id;}

    public String getImageUrl(){return imageUrl;}
    public void setImageUrl(String imageUrl){this.imageUrl= imageUrl;}

    public boolean isPrimaryImage(){return primaryImage;}
    public void setPrimaryImage(boolean primaryImage){this.primaryImage = primaryImage;}

    public int getDisplayOrder(){return displayOrder;}
    public void setDisplayOrder(int displayOrder){this.displayOrder = displayOrder;}

    public void setWig(Wig wig) { this.wig = wig; }
    public Wig getWig(){return wig;}
}
