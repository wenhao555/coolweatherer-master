package com.coolweather.android.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Weather2
{
    String resultcode;
    String reason;
    int error_code;
    Result result;

    public String getResultcode()
    {
        return resultcode;
    }

    public void setResultcode(String resultcode)
    {
        this.resultcode = resultcode;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public int getError_code()
    {
        return error_code;
    }

    public void setError_code(int error_code)
    {
        this.error_code = error_code;
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public static class Result
    {
        public Sk getSk()
        {
            return sk;
        }

        public void setSk(Sk sk)
        {
            this.sk = sk;
        }

        public Today getToday()
        {
            return today;
        }

        public void setToday(Today today)
        {
            this.today = today;
        }


        private Sk sk;
        private Today today;
        private List<Future> futures;

        public List<Future> getFutures()
        {
            return futures;
        }

        public void setFutures(List<Future> futures)
        {
            this.futures = futures;
        }


        public static class Sk
        {
            private String temp;
            private String wind_direction;
            private String wind_strength;

            public String getTemp()
            {
                return temp;
            }

            public void setTemp(String temp)
            {
                this.temp = temp;
            }

            public String getWind_direction()
            {
                return wind_direction;
            }

            public void setWind_direction(String wind_direction)
            {
                this.wind_direction = wind_direction;
            }

            public String getWind_strength()
            {
                return wind_strength;
            }

            public void setWind_strength(String wind_strength)
            {
                this.wind_strength = wind_strength;
            }

            public String getHumidity()
            {
                return humidity;
            }

            public void setHumidity(String humidity)
            {
                this.humidity = humidity;
            }

            public String getTime()
            {
                return time;
            }

            public void setTime(String time)
            {
                this.time = time;
            }

            private String humidity;
            private String time;
        }

        public static class Today
        {
            public String getTemperature()
            {
                return temperature;
            }

            public void setTemperature(String temperature)
            {
                this.temperature = temperature;
            }

            public String getWeather()
            {
                return weather;
            }

            public void setWeather(String weather)
            {
                this.weather = weather;
            }

            public Weather_id getWeather_id()
            {
                return weather_id;
            }

            public void setWeather_id(Weather_id weather_id)
            {
                this.weather_id = weather_id;
            }

            public String getWind()
            {
                return wind;
            }

            public void setWind(String wind)
            {
                this.wind = wind;
            }

            public String getWeek()
            {
                return week;
            }

            public void setWeek(String week)
            {
                this.week = week;
            }

            public String getCity()
            {
                return city;
            }

            public void setCity(String city)
            {
                this.city = city;
            }

            public String getDate_y()
            {
                return date_y;
            }

            public void setDate_y(String date_y)
            {
                this.date_y = date_y;
            }

            public String getDressing_index()
            {
                return dressing_index;
            }

            public void setDressing_index(String dressing_index)
            {
                this.dressing_index = dressing_index;
            }

            public String getDressing_advice()
            {
                return dressing_advice;
            }

            public void setDressing_advice(String dressing_advice)
            {
                this.dressing_advice = dressing_advice;
            }

            public String getUv_index()
            {
                return uv_index;
            }

            public void setUv_index(String uv_index)
            {
                this.uv_index = uv_index;
            }

            public String getComfort_index()
            {
                return comfort_index;
            }

            public void setComfort_index(String comfort_index)
            {
                this.comfort_index = comfort_index;
            }

            public String getWash_index()
            {
                return wash_index;
            }

            public void setWash_index(String wash_index)
            {
                this.wash_index = wash_index;
            }

            public String getTravel_index()
            {
                return travel_index;
            }

            public void setTravel_index(String travel_index)
            {
                this.travel_index = travel_index;
            }

            public String getExercise_index()
            {
                return exercise_index;
            }

            public void setExercise_index(String exercise_index)
            {
                this.exercise_index = exercise_index;
            }

            public String getDrying_index()
            {
                return drying_index;
            }

            public void setDrying_index(String drying_index)
            {
                this.drying_index = drying_index;
            }

            private String temperature;
            private String weather;
            private Weather_id weather_id;
            private String wind;
            private String week;
            private String city;
            private String date_y;
            private String dressing_index;
            private String dressing_advice;
            private String uv_index;
            private String comfort_index;
            private String wash_index;
            private String travel_index;
            private String exercise_index;
            private String drying_index;

            class Weather_id
            {
                private String fa;

                public String getFa()
                {
                    return fa;
                }

                public void setFa(String fa)
                {
                    this.fa = fa;
                }

                public String getFb()
                {
                    return fb;
                }

                public void setFb(String fb)
                {
                    this.fb = fb;
                }

                private String fb;
            }
        }

        public static class Future
        {

            private String temperature;
            private String weather;
            private String wind;
            private String week;

            public String getTemperature()
            {
                return temperature;
            }

            public void setTemperature(String temperature)
            {
                this.temperature = temperature;
            }

            public String getWeather()
            {
                return weather;
            }

            public void setWeather(String weather)
            {
                this.weather = weather;
            }

            public String getWind()
            {
                return wind;
            }

            public void setWind(String wind)
            {
                this.wind = wind;
            }

            public String getWeek()
            {
                return week;
            }

            public void setWeek(String week)
            {
                this.week = week;
            }

            public String getDate()
            {
                return date;
            }

            public void setDate(String date)
            {
                this.date = date;
            }

            private String date;

            private class Weather_id
            {
                private String fa;

                public String getFa()
                {
                    return fa;
                }

                public void setFa(String fa)
                {
                    this.fa = fa;
                }

                public String getFb()
                {
                    return fb;
                }

                public void setFb(String fb)
                {
                    this.fb = fb;
                }

                private String fb;
            }
        }

    }

}
