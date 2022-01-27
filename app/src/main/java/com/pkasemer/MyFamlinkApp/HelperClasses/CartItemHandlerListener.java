package com.pkasemer.MyFamlinkApp.HelperClasses;

import com.pkasemer.MyFamlinkApp.Models.FoodDBModel;

public interface CartItemHandlerListener {
    void increment(int qty, FoodDBModel foodDBModel);
    void decrement(int qty, FoodDBModel foodDBModel);
    void deletemenuitem(String foodMenu_id, FoodDBModel foodDBModel);
}
