import 'package:timezone/timezone.dart' as tz;
import 'package:timezone/data/latest.dart' as tz;
import 'package:intl/intl.dart';

void initTimeZone() {
  tz.initializeTimeZones();
}

class PSTTimeZoneConverter {
  static final pstLocation = tz.getLocation('America/Los_Angeles');

  static String convertToPST(DateTime dateTime) {
    final pstTime = tz.TZDateTime.from(dateTime, pstLocation);
    return '${DateFormat('dd MMM, yyyy HH:mm').format(pstTime)} PST';
  }
}
