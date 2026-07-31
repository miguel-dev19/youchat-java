package cu.alexgi.youchat.items;

import java.util.ArrayList;

public class ItemFolderSticker {

    private ArrayList<ItemSticker> stickers;

    public ItemFolderSticker() {
        this.stickers = new ArrayList<>();
    }

    public ItemFolderSticker(ArrayList<ItemSticker> s) {
        this.stickers = s;
    }

    public void addSticker(ItemSticker sticker){
        stickers.add(sticker);
    }

//    public InputStream imagenCarpeta(){
//        return stickers.get(0).getInputStream();
//    }

    public ItemSticker getStickerIn(int pos){
        if(pos<0 || pos>=stickers.size())
            return stickers.get(0);
        else return stickers.get(pos);
    }

    public ArrayList<ItemSticker> getStickers(){
        return stickers;
    }

    public void addStickers(ArrayList<ItemSticker> stickersAdd) {
        if(stickers==null) stickers = new ArrayList<>();
        stickers.addAll(stickersAdd);
    }
}
