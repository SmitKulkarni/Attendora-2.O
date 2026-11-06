"""
Attendora SaaS - GPS Geofencing Module
Calculates geographical distances using the Haversine formula to enforce physical presence.
"""
import math


def calculate_haversine_distance_meters(lat1, lon1, lat2, lon2):
    """
    Computes the great-circle distance between two GPS coordinates on Earth in meters.
    """
    R = 6371000.0  # Earth's mean radius in meters

    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lon2 - lon1)

    a = (math.sin(delta_phi / 2.0) ** 2 +
         math.cos(phi1) * math.cos(phi2) * (math.sin(delta_lambda / 2.0) ** 2))
    c = 2.0 * math.atan2(math.sqrt(a), math.sqrt(1.0 - a))

    return round(R * c, 2)


def verify_classroom_geofence(classroom_lat, classroom_lon, student_lat, student_lon, allowed_radius_meters=50.0):
    """
    Verifies if the student's GPS coordinate falls within the classroom perimeter.
    Returns: (is_inside: bool, distance_meters: float)
    """
    distance = calculate_haversine_distance_meters(
        classroom_lat, classroom_lon, student_lat, student_lon
    )
    is_inside = distance <= allowed_radius_meters
    return is_inside, distance

