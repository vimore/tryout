DEFINE field_summary_cmd `field_summary_streaming.py` SHIP('field_summary_streaming.py');
DEFINE field_summary(in_relation, field_name) RETURNS out_relation {
  counts = GROUP $in_relation BY $field_name GENERATE
    (chararray)group AS value:chararray,
    SUM(1) AS count:long;
  countmap = GROUP count;

  $out_relation = STREAM $countmap THROUGH field_summary_cmd AS
    (minimum:double,
     first_q:double,
     median:double,
     third_q:double,
     maximum:double,
     unique_count:long,
     entropy:double,
     top10:map);
}
