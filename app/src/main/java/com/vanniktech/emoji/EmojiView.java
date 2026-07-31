package com.vanniktech.emoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vanniktech.emoji.emoji.EmojiCategory;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickListener;
import com.vanniktech.emoji.listeners.OnEmojiLongClickListener;
import com.vanniktech.emoji.listeners.RepeatListener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.Inflater;

import cu.alexgi.youchat.ViewStickersFragment;
import cu.alexgi.youchat.adapters.AdaptadorDatosListaStickers;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemFolderSticker;
import cu.alexgi.youchat.items.ItemListaSticker;
import cu.alexgi.youchat.items.ItemSticker;

import static java.util.concurrent.TimeUnit.SECONDS;

@SuppressLint("ViewConstructor")
public final class EmojiView extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final long INITIAL_INTERVAL = SECONDS.toMillis(1) / 2;
    private static final int NORMAL_INTERVAL = 50;

    private EmojiView emojiView;
//    private File[] listFiles;
    private ArrayList<ItemFolderSticker> folderStickers;
    private Context c;

    @ColorInt
    private final int themeAccentColor;
    @ColorInt
    private final int themeIconColor;

    private final ImageButton[] emojiTabs;
    private final EmojiPagerAdapter emojiPagerAdapter;

    @Nullable
    OnEmojiBackspaceClickListener onEmojiBackspaceClickListener;

    private int emojiTabLastSelectedIndex = -1;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public EmojiView(final Context context,
                     final OnEmojiClickListener onEmojiClickListener,
                     final OnEmojiLongClickListener onEmojiLongClickListener, @NonNull final EmojiPopup.Builder builder,
                     boolean esChatActivity) {
        super(context);

        emojiView=this;
        c = context;
        View.inflate(context, R.layout.emoji_view, this);

        setOrientation(VERTICAL);
        setBackgroundColor(builder.backgroundColor != 0 ? builder.backgroundColor : Utils.resolveColor(context, R.attr.emojiBackground, R.color.emoji_background));
        themeIconColor = builder.iconColor != 0 ? builder.iconColor : Utils.resolveColor(context, R.attr.emojiIcons, R.color.emoji_icons);

        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        themeAccentColor = builder.selectedIconColor != 0 ? builder.selectedIconColor : value.data;

        final ViewPager emojisPager = findViewById(R.id.emojiViewPager);
        final View emojiDivider = findViewById(R.id.emojiViewDivider);
        emojiDivider.setBackgroundColor(builder.dividerColor != 0 ? builder.dividerColor : Utils.resolveColor(context, R.attr.emojiDivider, R.color.emoji_divider));

        if (builder.pageTransformer != null) {
            emojisPager.setPageTransformer(true, builder.pageTransformer);
        }

        final LinearLayout emojisTab = findViewById(R.id.emojiViewTab);
        emojisPager.addOnPageChangeListener(this);

        TabLayout tab_sticker = findViewById(R.id.tab_sticker);
        ViewPager2 stickerViewPager = findViewById(R.id.stickerViewPager);
        View more_sticker = findViewById(R.id.more_sticker);

        View ViewTab = findViewById(R.id.ViewTab);

        View ViewSticker= findViewById(R.id.ViewSticker);
        if(!esChatActivity)ViewSticker.setVisibility(GONE);

        View sticker = findViewById(R.id.sticker);
        sticker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stickerViewPager.getVisibility()!=VISIBLE)
                {
                    cu.alexgi.youchat.Utils.runOnUIThread(()->{
                        emojisTab.setVisibility(GONE);
                        emojisPager.setVisibility(GONE);
                        ViewTab.setVisibility(VISIBLE);
                        stickerViewPager.setVisibility(VISIBLE);

                        folderStickers = new ArrayList<>();
                        folderStickers.addAll(YouChatApplication.carpetasStickers);

                        if(YouChatApplication.chatsActivity!=null){
                            FragmentStateAdapter pagerAdapter
                                    = new ScreenSlidePagerAdapter(YouChatApplication.chatsActivity);
                            stickerViewPager.setAdapter(pagerAdapter);
                        }
                        else if(YouChatApplication.chatsActivityCorreo!=null){
                            FragmentStateAdapter pagerAdapter
                                    = new ScreenSlidePagerAdapter(YouChatApplication.chatsActivityCorreo);
                            stickerViewPager.setAdapter(pagerAdapter);
                        }

                        more_sticker.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
                                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_add_sticker);

                                RecyclerView lista_add_sticker = bottomSheetDialog.findViewById(R.id.lista_add_sticker);
                                String packsDescargados="";
                                File root = new File(YouChatApplication.RUTA_STICKERS);
                                if(root.exists()) {
                                    File[] carpetasReales = root.listFiles(new FileFilter() {
                                        @Override
                                        public boolean accept(File pathname) {
                                            return pathname.isDirectory();
                                        }
                                    });
                                    int l = carpetasReales.length;
                                    for(int i=0 ; i<l ; i++){
                                        if(!packsDescargados.equals("")) packsDescargados+=",";
                                        packsDescargados+=carpetasReales[i].getName();
                                    }
                                }
                                String [] listaDatos = {"AdvenTimeAnim", "animatedcontest_14", "AnimatedEmojies", "AnimatedPikachu", "animated_random", "Animated_White_Rabbit", "animaxolotl", "antivirusfighting_stickers", "Arachnid", "ArcticFox", "aviasales_stickers", "BananaFun", "Batoshik", "BestStickers_emojisrangii", "BigCock", "BigliMiglipack", "birdie_anim", "BlueBird", "BobJellyfish", "BoobSpongi", "BoysClub", "BreadToast", "bubocat", "buckybecky", "Buddy_Bear", "BunnyHazard", "Bunnyta", "Capyboy", "Cat2O", "Catcula", "Cestum_Emoji", "Cheburashka", "ChristmasDogs", "ChristmasOrnaments", "ChristmasRat", "CloudiaSheep", "cloudyanim", "CoffinDanceAnimated", "ConcernedFroge", "CorgiMuffin", "CreditCredit", "cupiman", "Cutefennecvf", "CuteNurse", "DaisyRomashka", "DeadlyPack", "diggy_anim", "DinDino", "DLOVEUOY", "DoctorBubonic", "DoggyShark", "DolphinDolph", "DrugStore", "EagleAndSnowy", "EarlWoolf", "EggYolk", "emoji_mini", "Emotionalpaca", "FishPrometheus", "FluffyLoafer", "FredThePug", "freshsuperheroes", "Friendly_Panda", "FrodoFerret", "FroggoInLove", "Frogita", "FunkyGoose", "Gadgets", "GagikTheDuck", "GentleSnails", "Ghostspack", "gifki", "gldfsh", "GreenLezard", "grumpy_tiger", "GrumpyTiggerrr", "Halloweenkin", "HammerheadShark", "HamsterBernard", "HappyNewDeer", "HarleyQuinn", "HarryGorilla", "Hedgehog_Ned", "HolyPoop", "HomeElectronics", "HotCherry", "HotDog", "husky_ulayka", "IrishGuy", "itstimepack", "JackTheParrot", "JohnnyBravo", "JonnyCapybara", "KaBoomPack", "KangarooFighter", "Koala", "KoalaBear", "LadyVampire", "Lamplover", "LilCifer", "LilPuppy", "Liner_man", "LittleCatto", "LorisLemur", "Marshmallow_Couple_by_cocopry", "MayaSet", "MelieTheCavy", "Meme_stickers_2", "Menhera_chan_by_cocopry", "Meow_by_mysticise", "MiaBunny", "MiBunny", "Milk_Mocha2_by_cocopry", "Milk_Mocha_by_cocopry", "MintyZebra", "Miss_Bunny", "modimated", "MonkeyMix", "MonkeyPuppet", "Moodpack2020", "MooingCow", "Mooncalfanimation", "More_Capoo", "MrAvocado", "MrBear", "MrCat", "MrCaterpillar", "MrClockwise", "MrCroco", "MrLemur", "MrLittlePrince", "MrPanda", "MrPugDog", "MrRat", "MrSeagull", "MrSeal", "MrSlothy", "MsWitchCat", "MuffinMan", "NeonPigeon", "NickWallowPig", "Numbers0_9", "OctoPaul", "OfficeTurkey", "OrangeDoggo", "Orangino", "OwledDidi", "owlfilm", "pancake_sourcream", "PandaEmic", "PaultheCat", "Penguinissimo", "PenguinsLoloPepe", "PineappleAbe", "PinkMarshmallow", "PirateHack", "PlagueDoctorHawk", "PokeAnimated", "Polar_Owl", "PoolFlamingo", "ppmini", "ppmini2", "PrincessBubblegum", "prtyparrot", "PumpkinCat", "Punymove", "QIWI_QIWI", "RainbowUnicorn", "razzberry_vk", "RedElizabeth", "Red_Squirrel", "ResistanceDog", "RickAndMorty", "RobinBird", "SabretoothCat", "Santa2020", "SeaKingdom", "senya_animated", "seseren", "ShaitanChick", "shareapack", "SharkBoss", "ShaunTheSheep", "Shpooky", "simpsons_anim", "Skull", "Smileys_people", "Snail", "SnappyCrab", "SnowBabbit", "Snowman", "spoiledrabbitanim", "StarPatrick", "SteampunkJulia", "StickySquares", "Stitch_by_cocopry", "stormtr", "SunAndCloud", "SweetyBee", "SweetySanta", "SweetyStrawberry", "TeddyBear", "TextAnimated", "TheCoffeeCup", "TheFoods", "TheLittleMole", "TheMoomintroll", "TheVirus", "TidyTieTom", "ToiletPaperBoy", "TonyStar", "trending_icons", "Trump", "tyan2d_anim", "UPStickersAnimados", "UtyaDuck", "ValentineCat", "VampireQueen", "veterok_dachshund", "VultureBird", "WalletCash", "WhiteCatAnimated", "WildElephant", "Worms", "xiaoliuyaa", "YourCapoo"};
                                ArrayList<ItemListaSticker> listaStickers = new ArrayList<>();
                                int l = listaDatos.length;
                                for(int i=0 ; i<l ; i++)
                                {
                                    boolean exist = packsDescargados.contains(listaDatos[i]);
                                    listaStickers.add(new ItemListaSticker(listaDatos[i], exist, false, 0));
                                }

                                AdaptadorDatosListaStickers adaptadorDatosListaStickers = new AdaptadorDatosListaStickers(context, listaStickers);
                                lista_add_sticker.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                lista_add_sticker.setAdapter(adaptadorDatosListaStickers);

                                bottomSheetDialog.show();
                                View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
                                BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
                                bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        });

                        new TabLayoutMediator(tab_sticker, stickerViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                final View layout = LayoutInflater.from(context).inflate(R.layout.layout_sticker_tab,null);
                                LottieAnimationView stickerAnimation = layout.findViewById(R.id.stickerAnimation);

                                ItemSticker stk = folderStickers.get(position).getStickerIn(0);
                                if(stk.isTGS()){
                                    File file = new File(stk.getRutaCache());
                                    if(file.exists()){
                                        try {
                                            InputStream inputStream = new FileInputStream(file);
                                            LottieTask<LottieComposition> l = LottieCompositionFactory
                                                    .fromJsonInputStream(inputStream, null);
                                            l.addListener(new LottieListener<LottieComposition>() {
                                                @Override
                                                public void onResult(LottieComposition result) {
                                                    stickerAnimation.setComposition(result);
                                                }
                                            });
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                                else{
                                    Glide.with(context)
                                            .load(stk.getRutaOriginal())
                                            .error(R.drawable.placeholder)
                                            .into(stickerAnimation);
                                }
                                tab.setCustomView(layout);
                            }
                        }).attach();
                    });

                }
            }
        });
        View emoji = findViewById(R.id.emoji);
        emoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emojisTab.setVisibility(VISIBLE);
                emojisPager.setVisibility(VISIBLE);
                ViewTab.setVisibility(GONE);
                stickerViewPager.setVisibility(GONE);
                more_sticker.setOnClickListener(null);
            }
        });

        final EmojiCategory[] categories = EmojiManager.getInstance().getCategories();

        emojiTabs = new ImageButton[categories.length + 2];
        emojiTabs[0] = inflateButton(context, R.drawable.emoji_recent, R.string.emoji_category_recent, emojisTab);
        for (int i = 0; i < categories.length; i++) {
            emojiTabs[i + 1] = inflateButton(context, categories[i].getIcon(), categories[i].getCategoryName(), emojisTab);
        }
        emojiTabs[emojiTabs.length - 1] = inflateButton(context, R.drawable.emoji_backspace, R.string.emoji_backspace, emojisTab);

        handleOnClicks(emojisPager);

        emojiPagerAdapter = new EmojiPagerAdapter(onEmojiClickListener, onEmojiLongClickListener, builder.recentEmoji, builder.variantEmoji);
        emojisPager.setAdapter(emojiPagerAdapter);

        final int startIndex = emojiPagerAdapter.numberOfRecentEmojis() > 0 ? 0 : 1;
        emojisPager.setCurrentItem(startIndex);
        onPageSelected(startIndex);
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(Fragment fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ViewStickersFragment.newInstance(c, folderStickers.get(position));
        }

        @Override
        public int getItemCount() {
            return folderStickers.size();
        }
    }

    private void handleOnClicks(final ViewPager emojisPager) {
        for (int i = 0; i < emojiTabs.length - 1; i++) {
            emojiTabs[i].setOnClickListener(new EmojiTabsClickListener(emojisPager, i));
        }

        emojiTabs[emojiTabs.length - 1].setOnTouchListener(new RepeatListener(INITIAL_INTERVAL, NORMAL_INTERVAL, new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (onEmojiBackspaceClickListener != null) {
                    onEmojiBackspaceClickListener.onEmojiBackspaceClick(view);
                }
            }
        }));
    }

    public void setOnEmojiBackspaceClickListener(@Nullable final OnEmojiBackspaceClickListener onEmojiBackspaceClickListener) {
        this.onEmojiBackspaceClickListener = onEmojiBackspaceClickListener;
    }

    private ImageButton inflateButton(final Context context, @DrawableRes final int icon, @StringRes final int categoryName, final ViewGroup parent) {
        final ImageButton button = (ImageButton) LayoutInflater.from(context).inflate(R.layout.emoji_view_category, parent, false);

        button.setImageDrawable(AppCompatResources.getDrawable(context, icon));
        button.setColorFilter(themeIconColor, PorterDuff.Mode.SRC_IN);
        button.setContentDescription(context.getString(categoryName));

        parent.addView(button);

        return button;
    }

    @Override
    public void onPageSelected(final int i) {
        if (emojiTabLastSelectedIndex != i) {
            if (i == 0) {
                emojiPagerAdapter.invalidateRecentEmojis();
            }

            if (emojiTabLastSelectedIndex >= 0 && emojiTabLastSelectedIndex < emojiTabs.length) {
                emojiTabs[emojiTabLastSelectedIndex].setSelected(false);
                emojiTabs[emojiTabLastSelectedIndex].setColorFilter(themeIconColor, PorterDuff.Mode.SRC_IN);
            }

            emojiTabs[i].setSelected(true);
            emojiTabs[i].setColorFilter(themeAccentColor, PorterDuff.Mode.SRC_IN);

            emojiTabLastSelectedIndex = i;
        }
    }

    @Override
    public void onPageScrolled(final int i, final float v, final int i2) {
        // No-op.
    }

    @Override
    public void onPageScrollStateChanged(final int i) {
        // No-op.
    }

    static class EmojiTabsClickListener implements OnClickListener {
        private final ViewPager emojisPager;
        private final int position;

        EmojiTabsClickListener(final ViewPager emojisPager, final int position) {
            this.emojisPager = emojisPager;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            emojisPager.setCurrentItem(position);
        }
    }
}
