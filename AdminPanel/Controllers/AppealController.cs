using AdmPanel.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace AdmPanel.Controllers
{
    public class AppealController : ApiController
    {
        AppealContext context = new AppealContext();

        // GET api/<controller>
        public List<Ad> Get()
        {
            List<Ad> ads = context.Ads.ToList();
            ads.Reverse();

            if (ads.Count >= 3)
            {
                return ads.GetRange(0, 3);
            }
            else
            {
                return ads.GetRange(0, ads.Count);
            }
            
        }

        // GET api/<controller>/5
        public string Get(int id)
        {
            return "value";
        }

        // POST api/<controller>
        public int Post([FromBody]Appeal value)
        {
            try
            {
                context.Appeals.Add(value);
                context.SaveChanges();

                return 1;
            }catch(Exception e)
            {
                return 0;
            }
        }

        // PUT api/<controller>/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/<controller>/5
        public void Delete(int id)
        {
        }
    }
}