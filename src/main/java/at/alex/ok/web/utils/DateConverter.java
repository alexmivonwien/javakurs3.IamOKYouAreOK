package at.alex.ok.web.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "dateConverter")
public class DateConverter implements Converter {

	private static final String DD_MM_YYYY = "dd-MMM-yyyy";
	private SimpleDateFormat yyyyConvertor;

	public DateConverter() {
		yyyyConvertor = new SimpleDateFormat(DD_MM_YYYY, Locale.ENGLISH);
		yyyyConvertor.setLenient(false);
	}

	//@Override
	public Object getAsObject(FacesContext fc, UIComponent component,
			String value) throws ConverterException {

		if (value == null || value.isEmpty()) {
			return new Date();
		}

		Date result = null;

		try {
			result = (Date) yyyyConvertor.parseObject(value);
		} catch (ParseException e) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Invalid date         format.", "Failed to convert "
							+ value + "");
			throw new ConverterException(message);
		}

		return result;
	}

	//@Override
	public String getAsString(FacesContext fc, UIComponent component,
			Object date) throws ConverterException {
		try {
			return yyyyConvertor.format((Date) date);
		} catch (NullPointerException e) {
			return null;
		}
	}

}