package pl.databucket.web.database;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.hibernate.engine.jdbc.Size;
import pl.databucket.exception.EmptyInputValueException;
import pl.databucket.exception.ExceededMaximumNumberOfCharactersException;
import pl.databucket.exception.IncorrectValueException;
import pl.databucket.exception.UnexpectedException;

public class FieldValidator {

	public static String validateDescription(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException, ExceededMaximumNumberOfCharactersException {
		if (map.containsKey(Column.DESCRIPTION)) {
			if (map.get(Column.DESCRIPTION) == null) {
				return "";
			} else {
				String description = (String) map.get(Column.DESCRIPTION);
				if (description != null && description.length() > SizeLimit.DESCRIPTION.getNumber()) {
					throw new ExceededMaximumNumberOfCharactersException(Column.DESCRIPTION, description, SizeLimit.DESCRIPTION.getNumber());
				} else
					return description;
			}
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.DESCRIPTION);
			else
				return null;
		}
	}

	public static String validateIcon(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(Column.ICON_NAME)) {
			String icon = (String) map.get(Column.ICON_NAME);
			return icon;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.ICON_NAME);
			else
				return "LocalOffer";
		}
	}

	public static Boolean validateEventStatus(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(Column.ACTIVE)) {
			Boolean status = (Boolean) map.get(Column.ACTIVE);
			return status;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.ACTIVE);
			else
				return false;
		}
	}

	public static String validateCreatedBy(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException, ExceededMaximumNumberOfCharactersException {
		if (map.containsKey(Column.CREATED_BY)) {
			String createdBy = (String) map.get(Column.CREATED_BY);
			if (createdBy.length() > SizeLimit.CREATED_BY.getNumber()) {
				throw new ExceededMaximumNumberOfCharactersException(Column.CREATED_BY, createdBy, SizeLimit.CREATED_BY.getNumber());
			} else
				return createdBy;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.CREATED_BY);
			else
				return null;
		}
	}

	public static String validateUpdatedBy(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException, ExceededMaximumNumberOfCharactersException {
		if (map.containsKey(Column.UPDATED_BY)) {
			String updatedBy = (String) map.get(Column.UPDATED_BY);
			if (updatedBy.length() > SizeLimit.UPDATED_BY.getNumber()) {
				throw new ExceededMaximumNumberOfCharactersException(Column.UPDATED_BY, updatedBy, SizeLimit.UPDATED_BY.getNumber());
			} else
				return updatedBy;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.UPDATED_BY);
			else
				return null;
		}
	}

	public static String validateGroupName(Map<String, Object> map, boolean obligatory) throws IncorrectValueException, EmptyInputValueException, ExceededMaximumNumberOfCharactersException {
		if (map.containsKey(Column.GROUP_NAME)) {
			String bucketName = (String) map.get(Column.GROUP_NAME);
			final Pattern pattern = Pattern.compile("[a-zA-Z0-9- ]+");
			if (!pattern.matcher(bucketName).matches()) {
				throw new IncorrectValueException("Invalid group name. The group name can contain (not diactric) letters, digits, dash character and space character.");
			} else if (bucketName.length() > SizeLimit.GROUP_NAME.getNumber()) {
				throw new ExceededMaximumNumberOfCharactersException(Column.GROUP_NAME, bucketName, SizeLimit.GROUP_NAME.getNumber());
			} else
				return bucketName;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.GROUP_NAME);
			else
				return null;
		}
	}

	public static String validateClassName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.CLASS_NAME, SizeLimit.CLASS_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	public static String validateBucketName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.BUCKET_NAME, SizeLimit.BUCKET_NAME.getNumber(), "[a-zA-Z0-9-]+");
	}

	public static void validateSort(String sort) throws IncorrectValueException {
		if (sort.startsWith("asc(") || sort.startsWith("desc(")) {
			final Pattern pattern = Pattern.compile("[asc|desc]+\\(.+\\)");
			if (!pattern.matcher(sort).matches()) {
				throw new IncorrectValueException("Invalid sorting. The sort param must equals a field or asc(field) or desc(field). By default is used ascending sorting. As a filed can be set column name or json properties path.");
			}
		}
	}

	public static List<Condition> validateFilter(String filter) throws IncorrectValueException {
		List<Condition> filterList = new ArrayList<>();
		String[] filters = filter.split("\\)\\(");
		for (String f : filters) {
			Condition condition = null;

			if (f.startsWith("("))
				f = f.substring(1);
			if (f.endsWith(")"))
				f = f.substring(0, f.length() -1);

			int first = f.indexOf(';');
			int second = f.indexOf(';', first + 1);
			int third = f.indexOf(';', second + 1);

			String field = f.substring(0, first);
			String operator = f.substring(first + 1, second);
			String type = f.substring(second + 1, third);
			String value = f.substring(third + 1, f.length());

			if (type.equals("numeric"))
				condition = new Condition(field, Operator.fromString(operator), Integer.parseInt(value));
			else if (type.equals("boolean"))
				condition = new Condition(field, Operator.fromString(operator), Boolean.parseBoolean(value));
			else if (type.equals("numeric_array")) {
				String[] items = value.split(",");
				Integer[] ids = new Integer[items.length];
				for (int i = 0; i < items.length; i++) {
					ids[i] = Integer.parseInt(items[i]);
				}
				condition = new Condition(field, Operator.fromString(operator), new ArrayList<Integer>(Arrays.asList(ids)));
			} else
				condition = new Condition(field, Operator.fromString(operator), value);

			filterList.add(condition);
		}

		return filterList;
	}

	public static String validateTagName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.TAG_NAME, SizeLimit.TAG_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	public static String validateTaskName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.TASK_NAME, SizeLimit.TASK_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	public static String validateEventName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.EVENT_NAME, SizeLimit.EVENT_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	public static String validateFilterName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.FILTER_NAME, SizeLimit.FILTER_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	private static String validateName(Map<String, Object> map, boolean obligatory, String fieldName, int max_num_char, String patternStr) throws Exception {
		if (map.containsKey(fieldName)) {
			String name = (String) map.get(fieldName);
			final Pattern pattern = Pattern.compile(patternStr);
			if (!pattern.matcher(name).matches()) {
				throw new IncorrectValueException("Invalid name [" + fieldName + "]. The name must match to the pattern: " + patternStr);
			} else if (name.length() > max_num_char) {
				throw new ExceededMaximumNumberOfCharactersException(fieldName, name, max_num_char);
			} else
				return name;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(fieldName);
			else
				return null;
		}
	}

	public static String validateColumnsName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.COLUMNS_NAME, SizeLimit.COLUMNS_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	public static String validateViewName(Map<String, Object> map, boolean obligatory) throws Exception {
		return validateName(map, obligatory, Column.VIEW_NAME, SizeLimit.VIEW_NAME.getNumber(), "[a-zA-Z0-9- ]+");
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> validateConditions(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(Column.CONDITIONS)) {
			return (List<Map<String, Object>>) map.get(Column.CONDITIONS);
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.CONDITIONS);
			else
				return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> validateEventSchedule(Map<String, Object> map, boolean obligatory, boolean validate) throws EmptyInputValueException, IncorrectValueException, ParseException {
		if (map.containsKey(Column.SCHEDULE)) {
			Map<String, Object> schedule = (Map<String, Object>) map.get(Column.SCHEDULE);
			Instant currentTime = Instant.now();

			// Verify dates
			boolean periodically = (boolean) schedule.get(Constants.PERIODICALLY);
			if (periodically) {
				Instant starts = getUTCDate((String) schedule.get(Constants.STARTS));

				if (!starts.isAfter(currentTime) && validate)
					throw new IncorrectValueException("The 'Starts' must be in the future!");
				boolean enabled_end = (boolean) schedule.get(Constants.ENABLE_ENDS);
				if (enabled_end) {
					Instant ends = getUTCDate((String) schedule.get(Constants.ENDS));
					if (!ends.isAfter(currentTime) && validate)
						throw new IncorrectValueException("The 'Ends' must be in the future!");
					if (!ends.isAfter(starts) && validate)
						throw new IncorrectValueException("The 'Ends' must be after the 'Starts'!");
				}
			} else {
				Instant starts = getUTCDate((String) schedule.get(Constants.STARTS));

				if (!starts.isAfter(currentTime) && validate)
					throw new IncorrectValueException("The execution date and time must be in the future!");
			}


			return schedule;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.SCHEDULE);
			else
				return null;
		}
	}

	private static Instant getUTCDate(String dateInString) throws ParseException {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		LocalDateTime dt = LocalDateTime.parse(dateInString, fmt);
		return dt.toInstant(ZoneOffset.UTC);
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> validateEventTasks(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(Column.TASKS)) {
			List<Map<String, Object>> tasks = (List<Map<String, Object>>) map.get(Column.TASKS);
			if (tasks.size() == 0)
				throw new EmptyInputValueException(Column.TASKS);
			else
				return tasks;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.TASKS);
			else
				return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> validateTaskConfiguration(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException, IncorrectValueException {
		final String ACTIONS = "actions";
		final String TYPE = "type";
		final String REMOVE = "remove";
		final String MODIFY = "modify";

		if (map.containsKey(Column.CONFIGURATION)) {
			LinkedHashMap<String, Object> configuration = (LinkedHashMap<String, Object>) map.get(Column.CONFIGURATION);

			// Validate conditions
			validateConditions(configuration, false);

			// Validate actions
			if (configuration.containsKey(ACTIONS)) {
				LinkedHashMap<String, Object> actions = (LinkedHashMap<String, Object>) configuration.get(ACTIONS);
				if (actions.containsKey(TYPE)) {
					String type = (String) actions.get(TYPE);
					if (!type.equals(REMOVE) && !type.equals(MODIFY))
						throw new IncorrectValueException("Actions type can be 'remove' or 'modify'. You set as '" + type + "'.");

				} else
					throw new EmptyInputValueException(TYPE);
			} else
				throw new EmptyInputValueException(ACTIONS);

			return configuration;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.CONFIGURATION);
			else
				return null;
		}
	}


	@SuppressWarnings({ "unchecked"})
	public static ArrayList<Integer> validateBuckets(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException, UnexpectedException {
		if (map.containsKey(Column.BUCKETS)) {
			try {
				return (ArrayList<Integer>) map.get(Column.BUCKETS);
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.BUCKETS);
			else
				return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Condition> validateListOfConditions(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(Column.CONDITIONS)) {
			List<Condition> conditions = new ArrayList<Condition>();
			List<Map<String, Object>> conditionsMap = (List<Map<String, Object>>) map.get(Column.CONDITIONS);
			for (Map<String, Object> conditionMap : conditionsMap)
				conditions.add(new Condition(conditionMap));

			return conditions;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.CONDITIONS);
			else
				return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> validateColumns(Map<String, Object> map, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(Column.COLUMNS)) {
			return (List<Map<String, Object>>) map.get(Column.COLUMNS);
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.COLUMNS);
			else
				return null;
		}
	}

	public static Integer validateNullableId(Map<String, Object> map, String field, boolean obligatory) throws EmptyInputValueException {
		if (map.containsKey(field)) {
			Integer value = (Integer) map.get(field);
			if (value == null)
				value = -1;
			return value;
		} else {
			if (obligatory)
				throw new EmptyInputValueException(field);
			else
				return null;
		}
	}

	public static Integer validateIndex(Map<String, Object> map, boolean obligatory) throws IncorrectValueException, EmptyInputValueException {
		if (map.containsKey(Column.INDEX)) {
			try {
				int index = (int) map.get(Column.INDEX);
				if (index > 0)
					return index;
				else
					throw new IncorrectValueException("The '" + Column.INDEX + "' must be equal or grater then 0!");
			} catch (NumberFormatException e) {
				throw new IncorrectValueException("The '" + Column.INDEX + "' must be a natural number!");
			}
		} else {
			if (obligatory)
				throw new EmptyInputValueException(Column.INDEX);
			else
				return 100;
		}
	}

	public static Integer validatePage(Map<String, Object> map, boolean obligatory) throws IncorrectValueException, EmptyInputValueException {
		String pageKey = "page";

		if (map.containsKey(pageKey)) {
			try {
				int page = (int) map.get(pageKey);
				if (page > 0)
					return page;
				else
					throw new IncorrectValueException("The '" + pageKey + "' must be grater then 0!");
			} catch (NumberFormatException e) {
				throw new IncorrectValueException("The '" + pageKey + "' must be a natural number!");
			}
		} else {
			if (obligatory)
				throw new EmptyInputValueException(pageKey);
			else
				return null;
		}
	}

	public static void mustBeGraterThen0(String fieldName, Integer value) throws IncorrectValueException {
		if (value <= 0)
			throw new IncorrectValueException("The '" + fieldName + "' must be grater then 0!");

	}

	public static void mustBeGraterOrEqual0(String fieldName, Integer value) throws IncorrectValueException {
		if (value < 0)
			throw new IncorrectValueException("The '" + fieldName + "' must be grater or equal to 0!");

	}

	public static Integer validateLimit(Map<String, Object> map, boolean obligatory) throws IncorrectValueException, EmptyInputValueException {
		String limitKey = "limit";

		if (map.containsKey(limitKey)) {
			try {
				int page = (int) map.get(limitKey);
				if (page > 0)
					return page;
				else
					throw new IncorrectValueException("The '" + limitKey + "' must be grater then 0!");
			} catch (NumberFormatException e) {
				throw new IncorrectValueException("The '" + limitKey + "' must be a natural number!");
			}
		} else {
			if (obligatory)
				throw new EmptyInputValueException(limitKey);
			else
				return null;
		}
	}

}
