package com.wigstore.wigstore.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "wigs")
public class Wig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

     @Column(nullable = false, length= 1000)
    private String description;

    @Enumerated(EnumType.STRING)
     @Column(nullable = false, precision = 10, scale= 2)
    private BigDecimal priceUsd;

     @Column(nullable = false)
    private String color;

     @Column(nullable = false)
    private Double lengthInches;

     @Column(nullable = false)
    private Double densityPercentage;

    //work on a way to notify the user that hairtype cannot be left blank
    @Enumerated(EnumType.STRING)
     @Column(nullable = false)
    private HairType hairType;

     @Column(nullable = false)
    private int stockQuantity;

     @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

     public Long getId() { return id; }


    public String getName() { return name; }
   public void setName(String name){this.name= name;}

   public String getDescription(){return description;}
   public void setDescription(String description){this.description= description;}

   public Currency getCurrency(){return currency;}
   public void setCurrency(Currency currency){this.currency= curency;}

   public String getColor(){return color;}
   public void setColor(String color){this.color= color;}

   public Double getLengthInches(){return lengthInches;}
   public void setLengthInches(Double lengthInches){this.lengthInches= lengthInches;}

  public Double getDensityPercentage(){return densityPercentage;}
  public void setDensityPercentage(Double densityPercentage){this.densityPercentage= densityPercentage;}

  public HairType getHairType(){return hairType;}
  public void setHairType( HairType hairType){this.hairType= hairType;}

  public int getStockQuantity(){return stockQuantity;}
  public void setStockQuantity(int stockQuantity){this.stockQuantity= stockQuantity;}

  public Boolean getActive(){return active;}
  public void setActive(Boolean active){this.active= active;}

    public LocalDateTime getCreatedAt(){return createdAt;}

    public User getCreatedBy(){return createdBy;}
    public void setCreatedBy(User createdBy){this.createdBy= createdBy;}

    
}
