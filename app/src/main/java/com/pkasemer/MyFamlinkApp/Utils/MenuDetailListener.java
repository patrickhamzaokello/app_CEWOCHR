package com.pkasemer.MyFamlinkApp.Utils;

import com.pkasemer.MyFamlinkApp.Models.FoodDBModel;
import com.pkasemer.MyFamlinkApp.Models.SelectedCategoryMenuItemResult;

public interface MenuDetailListener {
    void retryPageLoad();
    void incrementqtn(int qty, FoodDBModel foodDBModel);
    void decrementqtn(int qty, FoodDBModel foodDBModel);

    void addToCartbtn(SelectedCategoryMenuItemResult selectedCategoryMenuItemResult);
    void orderNowMenuBtn(SelectedCategoryMenuItemResult selectedCategoryMenuItemResult);

}
