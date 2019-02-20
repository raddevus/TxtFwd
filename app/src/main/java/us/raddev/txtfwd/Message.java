package us.raddev.txtfwd;

public class Message {

    public Message(String _originatingNumber){
        this._originatingNumber = _originatingNumber;
    }

    public Message(String _originatingNumber, int capturedNumberId){
        this._originatingNumber = _originatingNumber;
        this._capturedNumberId = capturedNumberId;
    }

    public Message(String _originatingNumber, String _body, String _dateTimeCreated, boolean isInbound) {
        this._originatingNumber = _originatingNumber;
        this._body = _body;
        this._dateTimeCreated = _dateTimeCreated;
        this.isInbound = isInbound;
    }

    public int get_capturedNumberId() {
        return _capturedNumberId;
    }

    public void set_capturedNumberId(int _capturedNumberId) {
        this._capturedNumberId = _capturedNumberId;
    }

    private int _capturedNumberId;

    private String _originatingNumber;

    public String get_originatingNumber() {
        return _originatingNumber;
    }

    public void set_originatingNumber(String _originatingNumber) {
        this._originatingNumber = _originatingNumber;
    }

    public String get_body() {
        return _body;
    }

    public void set_body(String _body) {
        this._body = _body;
    }

    public String get_dateTimeCreated() {
        return _dateTimeCreated;
    }

    public void set_dateTimeCreated(String _dateTimeCreated) {
        this._dateTimeCreated = _dateTimeCreated;
    }

    public boolean isInbound() {
        return isInbound;
    }

    public void setIsInbound(boolean isInbound) {
        this.isInbound = isInbound;
    }

    private String _body;
    private String _dateTimeCreated;
    private boolean isInbound;

    @Override
    public String toString(){
        return _originatingNumber;
    }

}
