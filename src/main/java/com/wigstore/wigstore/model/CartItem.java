package com.wigstore.wigstore.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name= "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "wig_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private int quantity= 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable= false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "wig_id", nullable= false)
    private Wig wig;

   

    private LocalDateTime addedAt= LocalDateTime.now();

     public Long getId(){return id;}
  
     public LocalDateTime getAddedAt() { return addedAt; }

    public int getQuantity(){return quantity;}
   public void setQuantity(int quantity){this.quantity= quantity;}

    public User getUser(){return user;}
   public void setUser(User user){this.user= user;}

    public Wig getWig(){return  wig;}
   public void setWig(Wig  wig){this.wig= wig;}
    
}
