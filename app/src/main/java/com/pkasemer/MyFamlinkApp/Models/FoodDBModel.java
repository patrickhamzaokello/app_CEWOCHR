package com.pkasemer.MyFamlinkApp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodDBModel  implements Parcelable {

    private Integer menuId;

    private String menuName;

    private Integer price;

    private String description;

    private Integer menuTypeId;

    private String menuImage;

    private String backgroundImage;

    private String ingredients;

    private Integer menuStatus;

    private String created;

    private String modified;

    private Integer rating;

    private Integer quantity;

    public FoodDBModel(Integer menuId, String menuName, Integer price, String description, Integer menuTypeId, String menuImage, String backgroundImage, String ingredients, Integer menuStatus, String created, String modified, Integer rating, Integer quantity) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.menuTypeId = menuTypeId;
        this.menuImage = menuImage;
        this.backgroundImage = backgroundImage;
        this.ingredients = ingredients;
        this.menuStatus = menuStatus;
        this.created = created;
        this.modified = modified;
        this.rating = rating;
        this.quantity = quantity;
    }

    protected FoodDBModel(Parcel in) {
        if (in.readByte() == 0) {
            menuId = null;
        } else {
            menuId = in.readInt();
        }
        menuName = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readInt();
        }
        description = in.readString();
        if (in.readByte() == 0) {
            menuTypeId = null;
        } else {
            menuTypeId = in.readInt();
        }
        menuImage = in.readString();
        backgroundImage = in.readString();
        ingredients = in.readString();
        if (in.readByte() == 0) {
            menuStatus = null;
        } else {
            menuStatus = in.readInt();
        }
        created = in.readString();
        modified = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readInt();
        }
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
    }

    public static final Creator<FoodDBModel> CREATOR = new Creator<FoodDBModel>() {
        @Override
        public FoodDBModel createFromParcel(Parcel in) {
            return new FoodDBModel(in);
        }

        @Override
        public FoodDBModel[] newArray(int size) {
            return new FoodDBModel[size];
        }
    };

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(Integer menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public String getMenuImage() {
        return menuImage;
    }

    public void setMenuImage(String menuImage) {
        this.menuImage = menuImage;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Integer getMenuStatus() {
        return menuStatus;
    }

    public void setMenuStatus(Integer menuStatus) {
        this.menuStatus = menuStatus;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (menuId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(menuId);
        }
        dest.writeString(menuName);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(price);
        }
        dest.writeString(description);
        if (menuTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(menuTypeId);
        }
        dest.writeString(menuImage);
        dest.writeString(backgroundImage);
        dest.writeString(ingredients);
        if (menuStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(menuStatus);
        }
        dest.writeString(created);
        dest.writeString(modified);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(rating);
        }
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
    }
}
