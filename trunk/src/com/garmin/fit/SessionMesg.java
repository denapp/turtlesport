////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Dynastream Innovations Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2013 Dynastream Innovations Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 5.20Release
// Tag = $Name: AKW5_200 $
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;


public class SessionMesg extends Mesg implements MesgWithEvent {


   public SessionMesg() {
      super(Factory.createMesg(MesgNum.SESSION));
   }

   public SessionMesg(final Mesg mesg) {
      super(mesg);
   }


   /**
    * Get message_index field
    * Comment: Selected bit is set for the current session.
    *
    * @return message_index
    */
   public Integer getMessageIndex() {
      return getFieldIntegerValue(254, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set message_index field
    * Comment: Selected bit is set for the current session.
    *
    * @param messageIndex
    */
   public void setMessageIndex(Integer messageIndex) {
      setFieldValue(254, 0, messageIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get timestamp field
    * Units: s
    * Comment: Sesson end time.
    *
    * @return timestamp
    */
   public DateTime getTimestamp() {
      return timestampToDateTime(getFieldLongValue(253, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD));
   }

   /**
    * Set timestamp field
    * Units: s
    * Comment: Sesson end time.
    *
    * @param timestamp
    */
   public void setTimestamp(DateTime timestamp) {
      setFieldValue(253, 0, timestamp.getTimestamp(), Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get event field
    * Comment: session
    *
    * @return event
    */
   public Event getEvent() {
      Short value = getFieldShortValue(0, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Event.getByValue(value);
   }

   /**
    * Set event field
    * Comment: session
    *
    * @param event
    */
   public void setEvent(Event event) {
      setFieldValue(0, 0, event.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get event_type field
    * Comment: stop
    *
    * @return event_type
    */
   public EventType getEventType() {
      Short value = getFieldShortValue(1, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return EventType.getByValue(value);
   }

   /**
    * Set event_type field
    * Comment: stop
    *
    * @param eventType
    */
   public void setEventType(EventType eventType) {
      setFieldValue(1, 0, eventType.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_time field
    *
    * @return start_time
    */
   public DateTime getStartTime() {
      return timestampToDateTime(getFieldLongValue(2, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD));
   }

   /**
    * Set start_time field
    *
    * @param startTime
    */
   public void setStartTime(DateTime startTime) {
      setFieldValue(2, 0, startTime.getTimestamp(), Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_position_lat field
    * Units: semicircles
    *
    * @return start_position_lat
    */
   public Integer getStartPositionLat() {
      return getFieldIntegerValue(3, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set start_position_lat field
    * Units: semicircles
    *
    * @param startPositionLat
    */
   public void setStartPositionLat(Integer startPositionLat) {
      setFieldValue(3, 0, startPositionLat, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_position_long field
    * Units: semicircles
    *
    * @return start_position_long
    */
   public Integer getStartPositionLong() {
      return getFieldIntegerValue(4, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set start_position_long field
    * Units: semicircles
    *
    * @param startPositionLong
    */
   public void setStartPositionLong(Integer startPositionLong) {
      setFieldValue(4, 0, startPositionLong, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sport field
    *
    * @return sport
    */
   public Sport getSport() {
      Short value = getFieldShortValue(5, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Sport.getByValue(value);
   }

   /**
    * Set sport field
    *
    * @param sport
    */
   public void setSport(Sport sport) {
      setFieldValue(5, 0, sport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sub_sport field
    *
    * @return sub_sport
    */
   public SubSport getSubSport() {
      Short value = getFieldShortValue(6, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return SubSport.getByValue(value);
   }

   /**
    * Set sub_sport field
    *
    * @param subSport
    */
   public void setSubSport(SubSport subSport) {
      setFieldValue(6, 0, subSport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_elapsed_time field
    * Units: s
    * Comment: Time (includes pauses)
    *
    * @return total_elapsed_time
    */
   public Float getTotalElapsedTime() {
      return getFieldFloatValue(7, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_elapsed_time field
    * Units: s
    * Comment: Time (includes pauses)
    *
    * @param totalElapsedTime
    */
   public void setTotalElapsedTime(Float totalElapsedTime) {
      setFieldValue(7, 0, totalElapsedTime, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_timer_time field
    * Units: s
    * Comment: Timer Time (excludes pauses)
    *
    * @return total_timer_time
    */
   public Float getTotalTimerTime() {
      return getFieldFloatValue(8, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_timer_time field
    * Units: s
    * Comment: Timer Time (excludes pauses)
    *
    * @param totalTimerTime
    */
   public void setTotalTimerTime(Float totalTimerTime) {
      setFieldValue(8, 0, totalTimerTime, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_distance field
    * Units: m
    *
    * @return total_distance
    */
   public Float getTotalDistance() {
      return getFieldFloatValue(9, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_distance field
    * Units: m
    *
    * @param totalDistance
    */
   public void setTotalDistance(Float totalDistance) {
      setFieldValue(9, 0, totalDistance, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_cycles field
    * Units: cycles
    *
    * @return total_cycles
    */
   public Long getTotalCycles() {
      return getFieldLongValue(10, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_cycles field
    * Units: cycles
    *
    * @param totalCycles
    */
   public void setTotalCycles(Long totalCycles) {
      setFieldValue(10, 0, totalCycles, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_strides field
    * Units: strides
    *
    * @return total_strides
    */
   public Long getTotalStrides() {
      return getFieldLongValue(10, 0, Profile.SubFields.SESSION_MESG_TOTAL_CYCLES_FIELD_TOTAL_STRIDES);
   }

   /**
    * Set total_strides field
    * Units: strides
    *
    * @param totalStrides
    */
   public void setTotalStrides(Long totalStrides) {
      setFieldValue(10, 0, totalStrides, Profile.SubFields.SESSION_MESG_TOTAL_CYCLES_FIELD_TOTAL_STRIDES);
   }

   /**
    * Get total_calories field
    * Units: kcal
    *
    * @return total_calories
    */
   public Integer getTotalCalories() {
      return getFieldIntegerValue(11, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_calories field
    * Units: kcal
    *
    * @param totalCalories
    */
   public void setTotalCalories(Integer totalCalories) {
      setFieldValue(11, 0, totalCalories, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_fat_calories field
    * Units: kcal
    *
    * @return total_fat_calories
    */
   public Integer getTotalFatCalories() {
      return getFieldIntegerValue(13, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_fat_calories field
    * Units: kcal
    *
    * @param totalFatCalories
    */
   public void setTotalFatCalories(Integer totalFatCalories) {
      setFieldValue(13, 0, totalFatCalories, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_speed field
    * Units: m/s
    * Comment: total_distance / total_timer_time
    *
    * @return avg_speed
    */
   public Float getAvgSpeed() {
      return getFieldFloatValue(14, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_speed field
    * Units: m/s
    * Comment: total_distance / total_timer_time
    *
    * @param avgSpeed
    */
   public void setAvgSpeed(Float avgSpeed) {
      setFieldValue(14, 0, avgSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_speed field
    * Units: m/s
    *
    * @return max_speed
    */
   public Float getMaxSpeed() {
      return getFieldFloatValue(15, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_speed field
    * Units: m/s
    *
    * @param maxSpeed
    */
   public void setMaxSpeed(Float maxSpeed) {
      setFieldValue(15, 0, maxSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_heart_rate field
    * Units: bpm
    * Comment: average heart rate (excludes pause time)
    *
    * @return avg_heart_rate
    */
   public Short getAvgHeartRate() {
      return getFieldShortValue(16, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_heart_rate field
    * Units: bpm
    * Comment: average heart rate (excludes pause time)
    *
    * @param avgHeartRate
    */
   public void setAvgHeartRate(Short avgHeartRate) {
      setFieldValue(16, 0, avgHeartRate, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_heart_rate field
    * Units: bpm
    *
    * @return max_heart_rate
    */
   public Short getMaxHeartRate() {
      return getFieldShortValue(17, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_heart_rate field
    * Units: bpm
    *
    * @param maxHeartRate
    */
   public void setMaxHeartRate(Short maxHeartRate) {
      setFieldValue(17, 0, maxHeartRate, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_cadence field
    * Units: rpm
    * Comment: total_cycles / total_timer_time if non_zero_avg_cadence otherwise total_cycles / total_elapsed_time
    *
    * @return avg_cadence
    */
   public Short getAvgCadence() {
      return getFieldShortValue(18, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_cadence field
    * Units: rpm
    * Comment: total_cycles / total_timer_time if non_zero_avg_cadence otherwise total_cycles / total_elapsed_time
    *
    * @param avgCadence
    */
   public void setAvgCadence(Short avgCadence) {
      setFieldValue(18, 0, avgCadence, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_running_cadence field
    * Units: strides/min
    *
    * @return avg_running_cadence
    */
   public Short getAvgRunningCadence() {
      return getFieldShortValue(18, 0, Profile.SubFields.SESSION_MESG_AVG_CADENCE_FIELD_AVG_RUNNING_CADENCE);
   }

   /**
    * Set avg_running_cadence field
    * Units: strides/min
    *
    * @param avgRunningCadence
    */
   public void setAvgRunningCadence(Short avgRunningCadence) {
      setFieldValue(18, 0, avgRunningCadence, Profile.SubFields.SESSION_MESG_AVG_CADENCE_FIELD_AVG_RUNNING_CADENCE);
   }

   /**
    * Get max_cadence field
    * Units: rpm
    *
    * @return max_cadence
    */
   public Short getMaxCadence() {
      return getFieldShortValue(19, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_cadence field
    * Units: rpm
    *
    * @param maxCadence
    */
   public void setMaxCadence(Short maxCadence) {
      setFieldValue(19, 0, maxCadence, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_running_cadence field
    * Units: strides/min
    *
    * @return max_running_cadence
    */
   public Short getMaxRunningCadence() {
      return getFieldShortValue(19, 0, Profile.SubFields.SESSION_MESG_MAX_CADENCE_FIELD_MAX_RUNNING_CADENCE);
   }

   /**
    * Set max_running_cadence field
    * Units: strides/min
    *
    * @param maxRunningCadence
    */
   public void setMaxRunningCadence(Short maxRunningCadence) {
      setFieldValue(19, 0, maxRunningCadence, Profile.SubFields.SESSION_MESG_MAX_CADENCE_FIELD_MAX_RUNNING_CADENCE);
   }

   /**
    * Get avg_power field
    * Units: watts
    * Comment: total_power / total_timer_time if non_zero_avg_power otherwise total_power / total_elapsed_time
    *
    * @return avg_power
    */
   public Integer getAvgPower() {
      return getFieldIntegerValue(20, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_power field
    * Units: watts
    * Comment: total_power / total_timer_time if non_zero_avg_power otherwise total_power / total_elapsed_time
    *
    * @param avgPower
    */
   public void setAvgPower(Integer avgPower) {
      setFieldValue(20, 0, avgPower, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_power field
    * Units: watts
    *
    * @return max_power
    */
   public Integer getMaxPower() {
      return getFieldIntegerValue(21, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_power field
    * Units: watts
    *
    * @param maxPower
    */
   public void setMaxPower(Integer maxPower) {
      setFieldValue(21, 0, maxPower, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_ascent field
    * Units: m
    *
    * @return total_ascent
    */
   public Integer getTotalAscent() {
      return getFieldIntegerValue(22, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_ascent field
    * Units: m
    *
    * @param totalAscent
    */
   public void setTotalAscent(Integer totalAscent) {
      setFieldValue(22, 0, totalAscent, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_descent field
    * Units: m
    *
    * @return total_descent
    */
   public Integer getTotalDescent() {
      return getFieldIntegerValue(23, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_descent field
    * Units: m
    *
    * @param totalDescent
    */
   public void setTotalDescent(Integer totalDescent) {
      setFieldValue(23, 0, totalDescent, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_training_effect field
    *
    * @return total_training_effect
    */
   public Float getTotalTrainingEffect() {
      return getFieldFloatValue(24, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_training_effect field
    *
    * @param totalTrainingEffect
    */
   public void setTotalTrainingEffect(Float totalTrainingEffect) {
      setFieldValue(24, 0, totalTrainingEffect, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get first_lap_index field
    *
    * @return first_lap_index
    */
   public Integer getFirstLapIndex() {
      return getFieldIntegerValue(25, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set first_lap_index field
    *
    * @param firstLapIndex
    */
   public void setFirstLapIndex(Integer firstLapIndex) {
      setFieldValue(25, 0, firstLapIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get num_laps field
    *
    * @return num_laps
    */
   public Integer getNumLaps() {
      return getFieldIntegerValue(26, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set num_laps field
    *
    * @param numLaps
    */
   public void setNumLaps(Integer numLaps) {
      setFieldValue(26, 0, numLaps, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get event_group field
    *
    * @return event_group
    */
   public Short getEventGroup() {
      return getFieldShortValue(27, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set event_group field
    *
    * @param eventGroup
    */
   public void setEventGroup(Short eventGroup) {
      setFieldValue(27, 0, eventGroup, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get trigger field
    *
    * @return trigger
    */
   public SessionTrigger getTrigger() {
      Short value = getFieldShortValue(28, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return SessionTrigger.getByValue(value);
   }

   /**
    * Set trigger field
    *
    * @param trigger
    */
   public void setTrigger(SessionTrigger trigger) {
      setFieldValue(28, 0, trigger.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get nec_lat field
    * Units: semicircles
    *
    * @return nec_lat
    */
   public Integer getNecLat() {
      return getFieldIntegerValue(29, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set nec_lat field
    * Units: semicircles
    *
    * @param necLat
    */
   public void setNecLat(Integer necLat) {
      setFieldValue(29, 0, necLat, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get nec_long field
    * Units: semicircles
    *
    * @return nec_long
    */
   public Integer getNecLong() {
      return getFieldIntegerValue(30, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set nec_long field
    * Units: semicircles
    *
    * @param necLong
    */
   public void setNecLong(Integer necLong) {
      setFieldValue(30, 0, necLong, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get swc_lat field
    * Units: semicircles
    *
    * @return swc_lat
    */
   public Integer getSwcLat() {
      return getFieldIntegerValue(31, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set swc_lat field
    * Units: semicircles
    *
    * @param swcLat
    */
   public void setSwcLat(Integer swcLat) {
      setFieldValue(31, 0, swcLat, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get swc_long field
    * Units: semicircles
    *
    * @return swc_long
    */
   public Integer getSwcLong() {
      return getFieldIntegerValue(32, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set swc_long field
    * Units: semicircles
    *
    * @param swcLong
    */
   public void setSwcLong(Integer swcLong) {
      setFieldValue(32, 0, swcLong, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get normalized_power field
    * Units: watts
    *
    * @return normalized_power
    */
   public Integer getNormalizedPower() {
      return getFieldIntegerValue(34, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set normalized_power field
    * Units: watts
    *
    * @param normalizedPower
    */
   public void setNormalizedPower(Integer normalizedPower) {
      setFieldValue(34, 0, normalizedPower, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get training_stress_score field
    * Units: tss
    *
    * @return training_stress_score
    */
   public Float getTrainingStressScore() {
      return getFieldFloatValue(35, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set training_stress_score field
    * Units: tss
    *
    * @param trainingStressScore
    */
   public void setTrainingStressScore(Float trainingStressScore) {
      setFieldValue(35, 0, trainingStressScore, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get intensity_factor field
    * Units: if
    *
    * @return intensity_factor
    */
   public Float getIntensityFactor() {
      return getFieldFloatValue(36, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set intensity_factor field
    * Units: if
    *
    * @param intensityFactor
    */
   public void setIntensityFactor(Float intensityFactor) {
      setFieldValue(36, 0, intensityFactor, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get left_right_balance field
    *
    * @return left_right_balance
    */
   public Integer getLeftRightBalance() {
      return getFieldIntegerValue(37, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set left_right_balance field
    *
    * @param leftRightBalance
    */
   public void setLeftRightBalance(Integer leftRightBalance) {
      setFieldValue(37, 0, leftRightBalance, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_stroke_count field
    * Units: strokes/lap
    *
    * @return avg_stroke_count
    */
   public Float getAvgStrokeCount() {
      return getFieldFloatValue(41, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_stroke_count field
    * Units: strokes/lap
    *
    * @param avgStrokeCount
    */
   public void setAvgStrokeCount(Float avgStrokeCount) {
      setFieldValue(41, 0, avgStrokeCount, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_stroke_distance field
    * Units: m
    *
    * @return avg_stroke_distance
    */
   public Float getAvgStrokeDistance() {
      return getFieldFloatValue(42, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_stroke_distance field
    * Units: m
    *
    * @param avgStrokeDistance
    */
   public void setAvgStrokeDistance(Float avgStrokeDistance) {
      setFieldValue(42, 0, avgStrokeDistance, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get swim_stroke field
    * Units: swim_stroke
    *
    * @return swim_stroke
    */
   public SwimStroke getSwimStroke() {
      Short value = getFieldShortValue(43, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return SwimStroke.getByValue(value);
   }

   /**
    * Set swim_stroke field
    * Units: swim_stroke
    *
    * @param swimStroke
    */
   public void setSwimStroke(SwimStroke swimStroke) {
      setFieldValue(43, 0, swimStroke.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get pool_length field
    * Units: m
    *
    * @return pool_length
    */
   public Float getPoolLength() {
      return getFieldFloatValue(44, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set pool_length field
    * Units: m
    *
    * @param poolLength
    */
   public void setPoolLength(Float poolLength) {
      setFieldValue(44, 0, poolLength, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get pool_length_unit field
    *
    * @return pool_length_unit
    */
   public DisplayMeasure getPoolLengthUnit() {
      Short value = getFieldShortValue(46, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return DisplayMeasure.getByValue(value);
   }

   /**
    * Set pool_length_unit field
    *
    * @param poolLengthUnit
    */
   public void setPoolLengthUnit(DisplayMeasure poolLengthUnit) {
      setFieldValue(46, 0, poolLengthUnit.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get num_active_lengths field
    * Units: lengths
    * Comment: # of active lengths of swim pool
    *
    * @return num_active_lengths
    */
   public Integer getNumActiveLengths() {
      return getFieldIntegerValue(47, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set num_active_lengths field
    * Units: lengths
    * Comment: # of active lengths of swim pool
    *
    * @param numActiveLengths
    */
   public void setNumActiveLengths(Integer numActiveLengths) {
      setFieldValue(47, 0, numActiveLengths, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_work field
    * Units: J
    *
    * @return total_work
    */
   public Long getTotalWork() {
      return getFieldLongValue(48, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_work field
    * Units: J
    *
    * @param totalWork
    */
   public void setTotalWork(Long totalWork) {
      setFieldValue(48, 0, totalWork, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_altitude field
    * Units: m
    *
    * @return avg_altitude
    */
   public Float getAvgAltitude() {
      return getFieldFloatValue(49, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_altitude field
    * Units: m
    *
    * @param avgAltitude
    */
   public void setAvgAltitude(Float avgAltitude) {
      setFieldValue(49, 0, avgAltitude, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_altitude field
    * Units: m
    *
    * @return max_altitude
    */
   public Float getMaxAltitude() {
      return getFieldFloatValue(50, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_altitude field
    * Units: m
    *
    * @param maxAltitude
    */
   public void setMaxAltitude(Float maxAltitude) {
      setFieldValue(50, 0, maxAltitude, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get gps_accuracy field
    * Units: m
    *
    * @return gps_accuracy
    */
   public Short getGpsAccuracy() {
      return getFieldShortValue(51, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set gps_accuracy field
    * Units: m
    *
    * @param gpsAccuracy
    */
   public void setGpsAccuracy(Short gpsAccuracy) {
      setFieldValue(51, 0, gpsAccuracy, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_grade field
    * Units: %
    *
    * @return avg_grade
    */
   public Float getAvgGrade() {
      return getFieldFloatValue(52, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_grade field
    * Units: %
    *
    * @param avgGrade
    */
   public void setAvgGrade(Float avgGrade) {
      setFieldValue(52, 0, avgGrade, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_pos_grade field
    * Units: %
    *
    * @return avg_pos_grade
    */
   public Float getAvgPosGrade() {
      return getFieldFloatValue(53, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_pos_grade field
    * Units: %
    *
    * @param avgPosGrade
    */
   public void setAvgPosGrade(Float avgPosGrade) {
      setFieldValue(53, 0, avgPosGrade, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_neg_grade field
    * Units: %
    *
    * @return avg_neg_grade
    */
   public Float getAvgNegGrade() {
      return getFieldFloatValue(54, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_neg_grade field
    * Units: %
    *
    * @param avgNegGrade
    */
   public void setAvgNegGrade(Float avgNegGrade) {
      setFieldValue(54, 0, avgNegGrade, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_pos_grade field
    * Units: %
    *
    * @return max_pos_grade
    */
   public Float getMaxPosGrade() {
      return getFieldFloatValue(55, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_pos_grade field
    * Units: %
    *
    * @param maxPosGrade
    */
   public void setMaxPosGrade(Float maxPosGrade) {
      setFieldValue(55, 0, maxPosGrade, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_neg_grade field
    * Units: %
    *
    * @return max_neg_grade
    */
   public Float getMaxNegGrade() {
      return getFieldFloatValue(56, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_neg_grade field
    * Units: %
    *
    * @param maxNegGrade
    */
   public void setMaxNegGrade(Float maxNegGrade) {
      setFieldValue(56, 0, maxNegGrade, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_temperature field
    * Units: C
    *
    * @return avg_temperature
    */
   public Byte getAvgTemperature() {
      return getFieldByteValue(57, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_temperature field
    * Units: C
    *
    * @param avgTemperature
    */
   public void setAvgTemperature(Byte avgTemperature) {
      setFieldValue(57, 0, avgTemperature, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_temperature field
    * Units: C
    *
    * @return max_temperature
    */
   public Byte getMaxTemperature() {
      return getFieldByteValue(58, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_temperature field
    * Units: C
    *
    * @param maxTemperature
    */
   public void setMaxTemperature(Byte maxTemperature) {
      setFieldValue(58, 0, maxTemperature, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_moving_time field
    * Units: s
    *
    * @return total_moving_time
    */
   public Float getTotalMovingTime() {
      return getFieldFloatValue(59, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_moving_time field
    * Units: s
    *
    * @param totalMovingTime
    */
   public void setTotalMovingTime(Float totalMovingTime) {
      setFieldValue(59, 0, totalMovingTime, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_pos_vertical_speed field
    * Units: m/s
    *
    * @return avg_pos_vertical_speed
    */
   public Float getAvgPosVerticalSpeed() {
      return getFieldFloatValue(60, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_pos_vertical_speed field
    * Units: m/s
    *
    * @param avgPosVerticalSpeed
    */
   public void setAvgPosVerticalSpeed(Float avgPosVerticalSpeed) {
      setFieldValue(60, 0, avgPosVerticalSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_neg_vertical_speed field
    * Units: m/s
    *
    * @return avg_neg_vertical_speed
    */
   public Float getAvgNegVerticalSpeed() {
      return getFieldFloatValue(61, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_neg_vertical_speed field
    * Units: m/s
    *
    * @param avgNegVerticalSpeed
    */
   public void setAvgNegVerticalSpeed(Float avgNegVerticalSpeed) {
      setFieldValue(61, 0, avgNegVerticalSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_pos_vertical_speed field
    * Units: m/s
    *
    * @return max_pos_vertical_speed
    */
   public Float getMaxPosVerticalSpeed() {
      return getFieldFloatValue(62, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_pos_vertical_speed field
    * Units: m/s
    *
    * @param maxPosVerticalSpeed
    */
   public void setMaxPosVerticalSpeed(Float maxPosVerticalSpeed) {
      setFieldValue(62, 0, maxPosVerticalSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_neg_vertical_speed field
    * Units: m/s
    *
    * @return max_neg_vertical_speed
    */
   public Float getMaxNegVerticalSpeed() {
      return getFieldFloatValue(63, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_neg_vertical_speed field
    * Units: m/s
    *
    * @param maxNegVerticalSpeed
    */
   public void setMaxNegVerticalSpeed(Float maxNegVerticalSpeed) {
      setFieldValue(63, 0, maxNegVerticalSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get min_heart_rate field
    * Units: bpm
    *
    * @return min_heart_rate
    */
   public Short getMinHeartRate() {
      return getFieldShortValue(64, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set min_heart_rate field
    * Units: bpm
    *
    * @param minHeartRate
    */
   public void setMinHeartRate(Short minHeartRate) {
      setFieldValue(64, 0, minHeartRate, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * @return number of time_in_hr_zone
    */
   public int getNumTimeInHrZone() {
      return getNumFieldValues(65, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get time_in_hr_zone field
    * Units: s
    *
    * @param index of time_in_hr_zone
    * @return time_in_hr_zone
    */
   public Float getTimeInHrZone(int index) {
      return getFieldFloatValue(65, index, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set time_in_hr_zone field
    * Units: s
    *
    * @param index of time_in_hr_zone
    * @param timeInHrZone
    */
   public void setTimeInHrZone(int index, Float timeInHrZone) {
      setFieldValue(65, index, timeInHrZone, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * @return number of time_in_speed_zone
    */
   public int getNumTimeInSpeedZone() {
      return getNumFieldValues(66, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get time_in_speed_zone field
    * Units: s
    *
    * @param index of time_in_speed_zone
    * @return time_in_speed_zone
    */
   public Float getTimeInSpeedZone(int index) {
      return getFieldFloatValue(66, index, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set time_in_speed_zone field
    * Units: s
    *
    * @param index of time_in_speed_zone
    * @param timeInSpeedZone
    */
   public void setTimeInSpeedZone(int index, Float timeInSpeedZone) {
      setFieldValue(66, index, timeInSpeedZone, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * @return number of time_in_cadence_zone
    */
   public int getNumTimeInCadenceZone() {
      return getNumFieldValues(67, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get time_in_cadence_zone field
    * Units: s
    *
    * @param index of time_in_cadence_zone
    * @return time_in_cadence_zone
    */
   public Float getTimeInCadenceZone(int index) {
      return getFieldFloatValue(67, index, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set time_in_cadence_zone field
    * Units: s
    *
    * @param index of time_in_cadence_zone
    * @param timeInCadenceZone
    */
   public void setTimeInCadenceZone(int index, Float timeInCadenceZone) {
      setFieldValue(67, index, timeInCadenceZone, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * @return number of time_in_power_zone
    */
   public int getNumTimeInPowerZone() {
      return getNumFieldValues(68, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get time_in_power_zone field
    * Units: s
    *
    * @param index of time_in_power_zone
    * @return time_in_power_zone
    */
   public Float getTimeInPowerZone(int index) {
      return getFieldFloatValue(68, index, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set time_in_power_zone field
    * Units: s
    *
    * @param index of time_in_power_zone
    * @param timeInPowerZone
    */
   public void setTimeInPowerZone(int index, Float timeInPowerZone) {
      setFieldValue(68, index, timeInPowerZone, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_lap_time field
    * Units: s
    *
    * @return avg_lap_time
    */
   public Float getAvgLapTime() {
      return getFieldFloatValue(69, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_lap_time field
    * Units: s
    *
    * @param avgLapTime
    */
   public void setAvgLapTime(Float avgLapTime) {
      setFieldValue(69, 0, avgLapTime, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get best_lap_index field
    *
    * @return best_lap_index
    */
   public Integer getBestLapIndex() {
      return getFieldIntegerValue(70, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set best_lap_index field
    *
    * @param bestLapIndex
    */
   public void setBestLapIndex(Integer bestLapIndex) {
      setFieldValue(70, 0, bestLapIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get min_altitude field
    * Units: m
    *
    * @return min_altitude
    */
   public Float getMinAltitude() {
      return getFieldFloatValue(71, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set min_altitude field
    * Units: m
    *
    * @param minAltitude
    */
   public void setMinAltitude(Float minAltitude) {
      setFieldValue(71, 0, minAltitude, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

}
