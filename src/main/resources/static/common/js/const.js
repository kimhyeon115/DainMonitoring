
/* 구글 차트 태그 아이디 */
const chartId = 'chart';

/* 테이블 맵핑 */
const TableSet = {
	/* event */
	false: {
		basic: {
			key: {
				SV: [
					'measured_at',
					'correction_val',
					'changed_val'
				]
			},
			column: {
				SV: {
					measured_at: '측정일시',
					correction_val: 'PVS',
					changed_val: 'kine',
				}
			}
		},
		average: {
			key: {
				SV: [
					'measurement_date',
					'max_changed_val',
					'avg_changed_val',
					'record_count'
				]
			},
			column: {
				SV: {
					measurement_date: '날짜',
					max_changed_val: '최대(kine)',
					avg_changed_val: '평균(kine)',
					record_count: '진동횟수'
				}
			}
		}
	},
	/* cycle */
	true: {
		basic: {
			key: {
				Battery: [
					'measured_at',
					'displace_val'
				],
				Temp: [
					'measured_at',
					'displace_val'
				],
				Humidity: [
					'measured_at',
					'displace_val'
				],
				CRACK: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				EL_BEAM: [
					'measured_at',
					'correction_val',
					'angle_val',
					'displace_val',
					'changed_val',
					'angular_txt'
				],
				WaterLevel: [
					'measured_at',
					'displace_val',
					'changed_val'
				],
				GroundWaterLevel: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				Tiltmeter: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				Displacement: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				OsDisplacement: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				LaserDisplacement: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				LoadCell: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				Distance: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				GroundSubsidence: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				IPI: [
					'measured_at',
					'angle_val',
					'changed_val',
					'angular_txt'
				],
				RailTrack_X: [
					'measured_at',
					'angle_val',
					'changed_val',
					'angular_txt'
				],
				RailTrack_Y: [
					'measured_at',
					'angle_val',
					'changed_val',
					'angular_txt'
				],
				WireDisplacement: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				VWStrainGauge: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				],
				LVDT: [
					'measured_at',
					'correction_val',
					'displace_val',
					'changed_val'
				]
			},
			column: {
				Battery: {
					measured_at: '측정일시',
					displace_val: '측정값(vdc)'
				},
				Temp: {
					measured_at: '측정일시',
					displace_val: '측정값(°C)'
				},
				Humidity: {
					measured_at: '측정일시',
					displace_val: '측정값(%RH)'
				},
				CRACK: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				EL_BEAM: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					angle_val: '각도(°)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)',
					angular_txt: '각변위'
				},
				WaterLevel: {
					measured_at: '측정일시',
					displace_val: '수위(m)',
					changed_val: '변화량(m)'
				},
				GroundWaterLevel: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '현재 수위(m)',
					changed_val: '변화량(m)'
				},
				Tiltmeter: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(°)',
					changed_val: '변화량(°)'
				},
				Displacement: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				OsDisplacement: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(°)',
					changed_val: '변화량(°)'
				},
				LaserDisplacement: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				LoadCell: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '하중(kg)',
					changed_val: '변화량(kg)'
				},
				Distance: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				GroundSubsidence: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				IPI: {
					measured_at: '측정일시',
					angle_val: '각도(°)',
					changed_val: '변화량(mm)',
					angular_txt: '각변위'
				},
				RailTrack_X: {
					measured_at: '측정일시',
					angle_val: '각도(°)',
					changed_val: '변화량(mm)',
					angular_txt: '각변위'
				},
				RailTrack_Y: {
					measured_at: '측정일시',
					angle_val: '각도(°)',
					changed_val: '변화량(mm)',
					angular_txt: '각변위'
				},
				WireDisplacement: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				VWStrainGauge: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				},
				LVDT: {
					measured_at: '측정일시',
					correction_val: '측정값(mv)',
					displace_val: '변위(mm)',
					changed_val: '변화량(mm)'
				}
			}
		},
		average: {
			key: {
				Battery: [
					'measurement_date',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				Temp: [
					'measurement_date',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				Humidity: [
					'measurement_date',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				CRACK: [
					'measurement_date',
					'max_correction_val',
					'max_displace_val',
					'max_changed_val',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val'
				],
				EL_BEAM: [
					'measurement_date',
					'max_correction_val',
					'max_angle_val',
					'max_displace_val',
					'max_changed_val',
					'max_angular_txt',
					'avg_correction_val',
					'avg_angle_val',
					'avg_displace_val',
					'avg_changed_val',
					'avg_angular_txt'
				],
				WaterLevel: [
					'measurement_date',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				GroundWaterLevel: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				Tiltmeter: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_correction_val',
					'max_displace_val',
					'max_changed_val'
				],
				Displacement: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				OsDisplacement: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				LaserDisplacement: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				LoadCell: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_displace_val',
					'max_changed_val'
				],
				Distance: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				GroundSubsidence: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_changed_val',
					'min_changed_val'
				],
				IPI: [
					'measurement_date',
					'avg_angle_val',
					'avg_changed_val',
					'max_angle_val',
					'max_changed_val'
				],
				RailTrack_X: [
					'measurement_date',
					'avg_angle_val',
					'avg_changed_val',
					'max_angle_val',
					'max_changed_val'
				],
				RailTrack_Y: [
					'measurement_date',
					'avg_angle_val',
					'avg_changed_val',
					'max_angle_val',
					'max_changed_val'
				],
				WireDisplacement: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_correction_val',
					'max_displace_val',
					'max_changed_val'
				],
				VWStrainGauge: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_correction_val',
					'max_displace_val',
					'max_changed_val'
				],
				LVDT: [
					'measurement_date',
					'avg_correction_val',
					'avg_displace_val',
					'avg_changed_val',
					'max_correction_val',
					'max_displace_val',
					'max_changed_val'
				]
			},
			column: {
				Battery: {
					measurement_date: '날짜',
					avg_changed_val: '평균 측정값(vdc)',
					max_changed_val: '최대 측정값(vdc)',
					min_changed_val: '최소 측정값(vdc)'
				},
				Temp: {
					measurement_date: '날짜',
					avg_changed_val: '평균 측정값(°C)',
					max_changed_val: '최대 측정값(°C)',
					min_changed_val: '최소 측정값(°C)'
				},
				Humidity: {
					measurement_date: '날짜',
					avg_changed_val: '평균 측정값(%RH)',
					max_changed_val: '최대 측정값(%RH)',
					min_changed_val: '최소 측정값(%RH)'
				},
				CRACK: {
					measurement_date: '날짜',
					max_correction_val: '최대 측정값(mv)',
					max_displace_val: '최대 변위(mm)',
					max_changed_val: '최대 변화량(mm)',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)'
				},
				EL_BEAM: {
					measurement_date: '날짜',
					max_correction_val: '최대 측정값(mv)',
					max_angle_val: '최대 각도(°)',
					max_displace_val: '최대 변위(mm)',
					max_changed_val: '최대 변화량(mm)',
					max_angular_txt: '최대 각변위',
					avg_correction_val: '평균 측정값(mv)',
					avg_angle_val: '평균 각도(°)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					avg_angular_txt: '평균 각변위'
				},
				WaterLevel: {
					measurement_date: '날짜',
					avg_displace_val: '평균 수위(m)',
					avg_changed_val: '평균 변화량(m)',
					max_changed_val: '최대 변화량(m)',
					min_changed_val: '최소 변화량(m)'
				},
				GroundWaterLevel: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 수위(m)',
					avg_changed_val: '평균 변화량(m)',
					max_changed_val: '최대 변화량(m)',
					min_changed_val: '최소 변화량(m)'
				},
				Tiltmeter: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(°)',
					avg_changed_val: '평균 변화량(°)',
					max_correction_val: '최대 측정값(mm)',
					max_displace_val: '최대 변위(°)',
					max_changed_val: '최대 변화량(°)'
				},
				Displacement: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_changed_val: '최대 변화량(mm)',
					min_changed_val: '최소 변화량(mm)'
				},
				OsDisplacement: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(°)',
					avg_changed_val: '평균 변화량(°)',
					max_changed_val: '최대 변화량(°)',
					min_changed_val: '최소 변화량(°)'
				},
				LaserDisplacement: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_changed_val: '최대 변화량(mm)',
					min_changed_val: '최소 변화량(mm)'
				},
				LoadCell: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 하중(kg)',
					avg_changed_val: '평균 변화량(kg)',
					max_displace_val: '최대 하중(kg)',
					max_changed_val: '최대 변화량(kg)'
				},
				Distance: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_changed_val: '최대 변화량(mm)',
					min_changed_val: '최소 변화량(mm)'
				},
				GroundSubsidence: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mv)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_changed_val: '최대 변화량(mm)',
					min_changed_val: '최소 변화량(mm)'
				},
				IPI: {
					measurement_date: '날짜',
					avg_angle_val: '평균 각도(°)',
					avg_changed_val: '평균 변화량(mm)',
					max_angle_val: '최대 각도(°)',
					max_changed_val: '최대 변화량(mm)'
				},
				RailTrack_X: {
					measurement_date: '날짜',
					avg_angle_val: '평균 각도(°)',
					avg_changed_val: '평균 변화량(mm)',
					max_angle_val: '최대 각도(°)',
					max_changed_val: '최대 변화량(mm)'
				},
				RailTrack_Y: {
					measurement_date: '날짜',
					avg_angle_val: '평균 각도(°)',
					avg_changed_val: '평균 변화량(mm)',
					max_angle_val: '최대 각도(°)',
					max_changed_val: '최대 변화량(mm)'
				},
				WireDisplacement: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mm)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_correction_val: '최대 측정값(mm)',
					max_displace_val: '최대 변위(mm)',
					max_changed_val: '최대 변화량(mm)'
				},
				VWStrainGauge: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mm)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_correction_val: '최대 측정값(mm)',
					max_displace_val: '최대 변위(mm)',
					max_changed_val: '최대 변화량(mm)'
				},
				LVDT: {
					measurement_date: '날짜',
					avg_correction_val: '평균 측정값(mm)',
					avg_displace_val: '평균 변위(mm)',
					avg_changed_val: '평균 변화량(mm)',
					max_correction_val: '최대 측정값(mm)',
					max_displace_val: '최대 변위(mm)',
					max_changed_val: '최대 변화량(mm)'
				}
			}
		}
	}
};
