using AdmPanel.Models;
using AdmPanel.Utils;
using PagedList;
using RestSharp;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace AdmPanel.Controllers
{
    public class HomeController : Controller
    {
        AppealContext appealContext = new AppealContext();
        RestResponse<List<int>> response;
        RestResponse<Post> post;

        int pageSize = 5;
        int pageNumber = 1;

        public HomeController()
        {
            response = MonitoringService.GetMonitoringService().GetSocialInfo();
        }

        public ActionResult HomePage(int? page)
        {
            pageNumber = (page ?? 1);

            ViewBag.UserAppeals = appealContext.Appeals.Count(i => i.AppealToUser);
            ViewBag.PostAppeals = appealContext.Appeals.Count(i => !i.AppealToUser);
            ViewBag.UserCount = response.Data[0];
            ViewBag.DoctorCount = response.Data[1];
            ViewBag.UserPosts = response.Data[2];
            ViewBag.DoctorPosts = response.Data[3];
            ViewBag.Tags = response.Data[4];
            ViewBag.Comments = response.Data[5];

            return View(appealContext.Appeals.ToList().ToPagedList(pageNumber, pageSize));
        }

        public ActionResult Details(int id, int id2)
        {
            ViewBag.Id2 = id2;
            post = MonitoringService.GetMonitoringService().GetPost(id);
            Post p = Newtonsoft.Json.JsonConvert.DeserializeObject<Post>(post.Content);
            return PartialView("Details", p);
        }

        public ActionResult AdView(int id)
        {
            return PartialView("AdView");
        }

        public ActionResult SaveAdView(Ad ad, HttpPostedFileBase uploadImage)
        {
            if (ModelState.IsValid && uploadImage != null)
            {
                byte[] imageData = null;
                using (var binaryReader = new BinaryReader(uploadImage.InputStream))
                {
                    imageData = binaryReader.ReadBytes(uploadImage.ContentLength);
                }

                ad.Image = imageData;
            }
            appealContext.Ads.Add(ad);
            appealContext.SaveChanges();
            return View("HomePage", appealContext.Appeals.ToList().ToPagedList(pageNumber, pageSize));
        }

        public ActionResult AdList(int? page)
        {
            int pageNumberAd = (page ?? 1);

            return View(appealContext.Ads.ToList().ToPagedList(pageNumberAd, pageSize));
        }

        public ActionResult AdDelete(int id)
        {
            appealContext.Ads.Remove(appealContext.Ads.Where(p => p.Id == id).ToList()[0]);
            appealContext.SaveChanges();
            return View("HomePage", appealContext.Appeals.ToList().ToPagedList(pageNumber, pageSize));
        }

        public ActionResult PostDelete(int id, int id2)
        {
            int Id = id;
            MonitoringService.GetMonitoringService().DeletePost(Id);

            appealContext.Appeals.Find(id2).AppealStatus = true;
            appealContext.SaveChanges();

            return View("HomePage", appealContext.Appeals.ToList().ToPagedList(pageNumber, pageSize));
        }

        public ActionResult PostReturn(int id, int id2)
        {
            appealContext.Appeals.Find(id2).AppealStatus = true;
            appealContext.SaveChanges();

            return View("HomePage", appealContext.Appeals.ToList().ToPagedList(pageNumber, pageSize));
        }
    }
}