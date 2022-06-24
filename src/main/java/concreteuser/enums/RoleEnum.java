package concreteuser.enums;

public enum RoleEnum
{
		Admin("Admin"), Operator("Operator"), Analyst("Analyst"), Moderator("Moderator"), Editor("Editor");

		RoleEnum(String title)
		{
				this.title = title;
		}

		private String title;

		public String getTitle()
		{
				return title;
		}

		public void setTitle(String title)
		{
				this.title = title;
		}

		public static RoleEnum getByString(String title)
		{
				if(title == null)
						return null;
				for(RoleEnum element : RoleEnum.values())
						if(element.getTitle().equalsIgnoreCase(title))
								return element;
				return null;
		}

		@Override
		public String toString()
		{
				return getTitle();
		}
}
