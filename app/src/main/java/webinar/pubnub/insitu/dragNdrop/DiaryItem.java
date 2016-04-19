package webinar.pubnub.insitu.dragNdrop;


import webinar.pubnub.insitu.model.Diary;

public class DiaryItem extends Item {

    Diary diary;

    public DiaryItem(int icon, int spans, String title, String description) {
        super(icon, spans, title, description);
    }
}
