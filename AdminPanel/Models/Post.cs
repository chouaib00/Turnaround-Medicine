using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AdmPanel.Models
{
    public class Post
    {
        public int Id { get; set; }
        public string Description { get; set; }
        public string UserName { get; set; }
        public byte[] Photo { get; set; }
    }
}