package net.java.cargotracker.interfaces.handling.file.batch;

public class EventItem {

    String completionTimeValue;
    String trackingIdValue;
    String voyageNumberValue;
    String unLocodeValue;
    String eventTypeValue;
    
    public EventItem(String completionTimeValue,String trackingIdValue,String voyageNumberValue,String unLocodeValue,String  eventTypeValue){
        this.completionTimeValue    =   completionTimeValue;
        this.trackingIdValue        =   trackingIdValue;
        this.voyageNumberValue      =   voyageNumberValue;
        this.unLocodeValue          =   unLocodeValue;
        this.eventTypeValue         =   eventTypeValue;
    }
    
    public EventItem(String line){
        String[] result = line.split(",");
        this.completionTimeValue    =   result[0];
        this.trackingIdValue        =   result[1];
        this.voyageNumberValue      =   result[2];
        this.unLocodeValue          =   result[3];
        this.eventTypeValue         =   result[4];
    }
    
    public void setCompletionTimeValue(String completionTimeValue){
        this.completionTimeValue = completionTimeValue;
    }
    
    public String getCompletionTimeValue(){
        return this.completionTimeValue;
    }
    
    public void setTrackingIdValue(String trackingIdValue){
        this.trackingIdValue = trackingIdValue;
    }
    
    public String getTrackingIdValue(){
        return this.trackingIdValue;
    }
    
    public void setVoyageNumberValue(String voyageNumberValue){
        this.voyageNumberValue = voyageNumberValue;
    }
    
    public String getVoyageNumberValue(){
        return this.voyageNumberValue;
    }
    
    public void setUnLocodeValue(String unLocodeValue){
        this.unLocodeValue = unLocodeValue;
    }
    
    public String getUnLocodeValue(){
        return this.unLocodeValue;
    }
    
    public void setEventTypeValue(String eventTypeValue){
        this.eventTypeValue = eventTypeValue;
    }
    
    public String getEventTypeValue(){
        return this.eventTypeValue;
    }
    
    public String toString(){
        return "Event Details : Completion Time -> "    + this.completionTimeValue + ";" +
                                "  Tracking ID -> "     + this.trackingIdValue + 
                                "  Voyage ->"           + this.voyageNumberValue +
                                "  Location ->"         + this.unLocodeValue +
                                "  Event Type ->"       + this.eventTypeValue;
    }
    
    
    
}
