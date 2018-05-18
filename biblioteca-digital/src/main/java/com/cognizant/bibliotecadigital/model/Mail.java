package com.cognizant.bibliotecadigital.model;

import java.util.List;
import java.util.Map;

public class Mail {

    private String from;
    private String to;
    private String replyto;
    private String subject;
    private List<Object> attachments;
    private Map<String, Object> model;

    public Mail() {

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

	public String getReplyto() {
		return replyto;
	}

	public void setReplyto(String replyto) {
		this.replyto = replyto;
	}

	@Override
	public String toString() {
		return "Mail [from=" + from + ", to=" + to + ", replyto=" + replyto + ", subject=" + subject + ", attachments="
				+ attachments + ", model=" + model + "]";
	}

	

	
}
