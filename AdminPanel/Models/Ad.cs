using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AdmPanel.Models
{
    public class Ad
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public byte[] Image { get; set; }
        public string Url { get; set; }
    }
}