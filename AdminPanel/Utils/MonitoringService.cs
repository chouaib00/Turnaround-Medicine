using Retrofit.Net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AdmPanel.Utils
{
    public class MonitoringService
    {
        private const string BaseUrl = "http://node53284-env-7919692.mycloud.by/";
        private static RestAdapter restAdapter = new RestAdapter(BaseUrl);

        private MonitoringService() { }

        public static IMonitoringService GetMonitoringService()
        {
            return restAdapter.Create<IMonitoringService>();
        }
    }
}